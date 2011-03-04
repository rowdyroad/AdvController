<?
class Utils
{
    
    static function Dbg($cmd)
    {
        $dbg = debug_backtrace();                
        
        if (count($dbg) > 1)
        {                
            $data = $dbg[1];
            printf("%s %s::%s\t%s\n",date("Y-m-d H:i:s"), $data["class"], $data["function"], $cmd);
        }
        else
        {
            $data = $dbg[0];
            $p = pathinfo($data["file"]);
            printf("%s %s:%s\t%s\n",date("Y-m-d H:i:s"), $p["basename"], $data["line"], $cmd);
        }        
    }

    static function Execute($cmd, &$ret = null)
    {
        ob_start();
        system($cmd, $ret);
        $data = ob_get_contents();
        ob_end_clean();
        return $data;
    }

    static function LoadIniFile($filename, $args)
    {
        $f = fopen($filename, "w");
        foreach ($args as $name => $value)
        {
            fputs($f, "$name=$value\n");
        }
        fclose($f);
    }

    static function ReadIniFile($filename)
    {
        if (!file_exists($filename))
            return false;
        return parse_ini_file($filename);
    }

    static function Run($cmd, &$ret = null)
    {
        $cmd = str_replace("|", "\|", $cmd);
        $cmd = str_replace("&", "\&", $cmd);
        return execute($cmd, $ret);
    }

    static function HttpPost($url, $params = array(), &$err = null)
    {
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_FAILONERROR, 1);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        curl_setopt($ch, CURLOPT_TIMEOUT, 10);
        curl_setopt($ch, CURLOPT_POST, 1);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $params);
        $result = curl_exec($ch);
        $err = curl_errno($ch);
        if ($err != 0)
        {
            curl_close($ch);
            return false;
        }
        curl_close($ch);
        return $result;
    }

    static function Request($url, $params = array(), &$err = null)
    {
        ksort($params);
        $query = "";
        foreach ($params as $key => $value)
        {
            $query .= "$key=$value&";
        }
        $query = rtrim($query, "&");
        $params["sign"] = sha1($query . "_" . SECRET_KEY);
        return self::HttpPost($url, $params, $err);
    }

    static function RequestJSON($url, $params = array())
    {
        $r = self::Request($url, $params, $ret);
        if ($ret != 0)
            return false;
        $data = json_decode($r);
        if (!$data)
            return false;
        return $data;
    }

    static function Lock($filename)
    {
        $f = fopen($filename, "r");
        if (!$f)
            return false;
        if (flock($f, LOCK_EX))
        {
            return $f;
        }
        fclose($f);
        return false;
    }

    static function TryLock($filename)
    {
        $f = fopen($filename, "r");
        if (!$f)
            return false;
        if (flock($f, LOCK_EX | LOCK_NB))
        {
            return $f;
        }
        fclose($f);
        return false;
    }

    static function Unlock($f)
    {
        flock($f, LOCK_UN);
        fclose($f);
    }
    
    static private $unique_id_ = false;
    static function GetUniqueID()
    {
        if (!self::$unique_id_)
        {
            $data = Utils::Execute("fsutil fsinfo ntfsinfo C:");
            if (preg_match("/0x([0-9a-f]+)/i",$data,$regs))
            {
                self::$unique_id_ = $regs[1];    
            }
        }
        return self::$unique_id_;
    }
    
    
    
    static private function LoadConfigFile($param = "-c")
    {
        global $argv;
        for ($i = 0; $i < count($argv); ++$i)
        {
            if ($argv[$i] == $param)
            {
                return json_decode(file_get_contents($argv[$i + 1]));
            }    
        }        
        return false;
    }
    
    static private $config_ = null;
    static function Config()
    {
        if (!self::$config_)
        {
            if (! self::$config_ = Utils::LoadConfigFile())
            {
                 die("Couldn't load config file");
            }
        }   
        return self::$config_;
    }
    
}

?>