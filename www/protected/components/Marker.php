<?
class Marker {
    function GetTimeLength($filename) 
    {
        if (!file_exists($filename)) { 
            return false;
        }        
        return intval(`/usr/local/bin/soxi -D $filename`);
    }
    
    function GetNewId($length)
    {
        $r = Yii::app()->db->createCommand("select ident from promo_idents")->queryColumn();
        if (count($r) >= 253)
        {
    	    return false;
        }
        $ident = 0;
        while (true)
        {
            $i = rand(1, 255);
            if ($i == 15) continue;
            $r = Yii::app()->db->createCommand("select 1 from promo_idents where ident = '$i'")->queryAll();
            if (empty($r))
            { 
        	   return $i;
            }
        }
        return false;
    }
    
    function Mark($filename, $new_filename, $ident) 
    {
       $h_ident = str_pad(dechex($ident), 2, "0", STR_PAD_LEFT); 
        if ($f = popen("/home/admin/data/bin/watermark $filename $new_filename $h_ident 2>&1","r")) {
         while (!feof($f)) {
                if (preg_match("/^=100/",fgets($f))) {
                    pclose($f);
                    return true;
                }
            }
         pclose($f);
        }
        return false;
    }
    
    function Detect($filename)
    {
        if (! file_exists($filename)) { 
            return false;
        }
        
         if ($f = popen("/home/admin/data/bin/detect $filename 2>&1","r")) {
            $data = Array();
            while (!feof($f)) {
                $read = fgets($f);
                if (empty($read)) continue;                
                if (preg_match("/^\+ ([0-9\.]+) ([0-9]+)/",$read,$regs)) {
                    pclose($f);
                    return $regs[2];
                }
            }
            pclose($f);
         }         
         return false;
    }
    
    
}
?>