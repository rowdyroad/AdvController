#!/usr/bin/php
<?
    require_once('include/common.php');
    require_once('include/config.php');

    function setCaptureDevice($card, $device)
    {
        execute("amixer -c $card sset $device cap");
    }

    function getCurrentState($card, $input)
    {
    	$ret = Array();
    	$r = execute("amixer -c $card get $input,0");
    	if (preg_match("/Limits: .* ([0-9]+) - ([0-9]+)/i",$r,$regs))
    	{
    	    $ret["limits"] = Array("start"=>$regs[1],"stop"=>$regs[2]);
    	}
    	if (preg_match("/Front Left: $input ([0-9]+)/i",$r,$regs))
    	{
    	    $ret["volume"]["left"] = $regs[1];
    	}
    	if (preg_match("/Front Right: $input ([0-9]+)/i",$r,$regs))
    	{
    	    $ret["volume"]["right"] = $regs[1];
    	}
    	return $ret;
    }
    
    function setVolume($card, $input, $left_vol, $right_vol)
    {
        execute("amixer -c hw:$card sset $input $left_vol,$right_vol");
    }
    
    function volumeCalibration($card, $input, $channel, $needle)
    {
        dbg("Start volume calibration");
        $state = getCurrentState($card, $input);
        dbg("Capture volume limits: ".$state["limits"]["start"]." - ".$state["limits"]["stop"]);
        dbg("Current volume levels: left - ".$state["volume"]["left"]." right - ".$state["volume"]["right"]);
        $m = 0xffff;
        for ($i = $state["limits"]["start"]; $i <= $state["limits"]["stop"]; ++$i)
        {
            $last_left = $state['volume']['left'];
            $last_right = $state['volume']['right'];
            
            dbg("Test volume: $i");
            $state['volume'][$channel] = $i;
        	setVolume($input, $state['volume']['left'], $state['volume']['right']);
        	$r = execute("arecord -c plughw:{$card} -f cd -t raw -d 2 2>/dev/null | java -jar ".BIN_DIR."/calibrator.jar -t volume -c $channel");
        	if (preg_match("/max: ([\-0-9]+)/",$r,$regs))
        	{
        	    dbg("Needle dB: $needle / Max dB: $regs[1]");
        	    if ($m < abs($needle - $regs[1]))
        	    {
        		  dbg("Volume is ".($i - 1));
    	        	    setVolume($card, $input, $last_left,$last_right);
                	return $i - 1;
        	    }
        	    $m = abs($needle - $regs[1]);
     	    }
        }
        return $state['limits']['stop'];
        
        
    }
    
    function frequencyCalibration($card, $channel, $repeats, $seconds)
    {
    	dbg("Start frequency calibration");
    	$min_frequency = 0xffff;
    	$max_frequency = 0;
    	for ($i = 0; $i < $repeats; ++$i)
    	{
    	    dbg("Listening $seconds seconds...");  
    	    $r = execute("arecord -c plughw:{$card} -f cd -t raw -d $seconds 2>/dev/null | java -jar ".BIN_DIR."/calibrator.jar -t frequency -c $channel");
    	    if (preg_match("/frequency: ([0-9]+) - ([0-9]+)/",$r,$regs))
    	    {
        		$min_frequency = min($min_frequency,$regs[1]);
        		$max_frequency = max($max_frequency,$regs[2]);
     	    }
    	}
    	    	
    	dbg("Frequency: $min_frequency - $max_frequency");
    	dbg("End frequency calibration");
        
        return Array("min_frequency"=>$min_frequency, "max_frequency"=>$max_frequency);
    }
    
    function commitSettings($device, $input, $channel, $volume, $min_frequency,$max_frequency)
    {
    	$r = "";
    	if (! $f = fopen(ETC_DIR."/streamer.ini","r")) return false;
    	
    	while (!feof($f))
    	{
    	    $s = fgets($f);
    	    if (!preg_match("/{$channel}_(min|max)_frequency/",$s))
    	    {
    		  $r.=$s;
    	    }
    	}
    	fclose($f);
    	$r.="{$channel}_min_frequency=$min_frequency\n{$channel}_max_frequency=$max_frequency\n";        
        file_put_contents(ETC_DIR."/streamer.ini",$r);
        
        createIniFile(ETC_DIR."/etc/$card_$channel.ini",
    			Array(
    				"name"=>strtoupper(NAME.":1L:".$channel[0]),
    				"device"=>$device,
    				"input"=>$input,
    				"channel"=>$channel,
    				"card"=>$card,
    				"volume"=>$volume,
    				"min_frequency"=>$min_frequency,
    				"max_frequency"=>$max_frequency
    			    ));
    }

    print_r(getCurrentState(0,"\"Line in\""));
/*    setCaptureDevice("Line");
    $volume = volumeCalibration("Capture","left",-9);
    $range = frequencyCalibration("left",5,10);
    commitSettings("Line","Capture","left",$volume, $range["min_frequency"],$range["max_frequency"]);*/
?>

