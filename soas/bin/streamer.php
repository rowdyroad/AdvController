<?
    	require_once('include/config.php');
    	require_once('include/utils.class.php');
	Utils::Dbg("Recording to java");
   
/*
	$channels = Array(
				"l"=>Array("key"=>26,"min_freq"=>40,"max_freq"=>4000),
				//"r"=>Array("key"=>27,"min_freq"=>40,"max_freq"=>4000)
				);
	

	$ep = '"php '.BASE_DIR.'/bin/addevent.php {key} {id} {timestamp}"';
	
	$run = "java -jar ".BASE_DIR."/bin/streamer.jar -kg -40 -L 15 -e 0.8 -oc 4 -i true -st ".BASE_DIR."/data -ep $ep";
 
	foreach ($channels as $id=>$channel)
	{
		$key = $channel['key'];
		$min_freq = $channel['min_freq'];
		$max_freq = $channel['max_freq'];
		$run.= " -{$id}k $key -{$id}f $min_freq -".strtoupper($id)."F $max_freq";
	}
	

	//$run.=" >>  ".BASE_DIR."/log/streamer_".date("Y-m-d_H.i.s").".log";
*/
	
	$run = "java -jar ".BASE_DIR."/bin/streamer.jar -c ".BASE_DIR."/etc/config.ini";
	$run.="  >>  ".BASE_DIR."/log/streamer_".date("Y-m-d_H.i.s").".log";

	while (true)
	{
		system($run);
		sleep(2);
	}
	
?>