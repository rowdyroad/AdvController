<?


    class Updater
    {
        private $base_dir;
        private $unique_id;
        private $update_url;
        private $version = 0;
        
        function Updater($base_dir, $update_url, $unique_id)
        {
            $this->base_dir = $base_dir;
            $this->unique_id = $unique_id;
            $this->update_url = $update_url;
            $this->version = @file_get_contents($this->base_dir.DIRECTORY_SEPARATOR."run".DIRECTORY_SEPARATOR."version");
            
        }
        
        function Process()
        {
            if (!$list = Utils::RequestJSON($this->update_url,array("version"=>$this->version,"unique_id"=>$this->unique_id)))
            {
                return false;
            }
            
            if ($this->version < $list->version) return false;
                                   
            foreach ($list->items as $item)
            {
               $action = $item['action'];               
               if (method_exists($this, "action_$action"))
               {
                    if (!call_user_func(array($this,"action_$action", $item['data'])))
                    {
                      break;
                    }
                    ++$this->version;  
               }              
            }
            
            Utils::RequestJSON($this->update_url,array("new_version"=>$this->version,"unique_id"=>$this->unique_id));
            file_put_contents($this->base_dir.DIRECTORY_SEPARATOR."run".DIRECTORY_SEPARATOR."version", $this->version);
        }
        
        function action_mkdir($value)
        {
            $dirs = preg_split("/\\/",$value);
            
            $path = $this->base_dir;
            foreach ($dirs as $dir)
            {                
                $path .= DIRECTORY_SEPARATOR.$dir;
                
                if (!file_exists($path))
                {
                    if (!mkdir($path))
                    {
                        return false;   
                    }
                }
            }
            return true;
        }
        
        function action_rmdir($value)
        {
            return @rmdir($value);
        }
        
        function action_create_file($value)
        {
            return @file_put_contents($this->base_dir.DIRECTORY_SEPARATOR.$value['name'],(!empty($value['data'])) ? $value['data'] : "");                
        }
        
        function action_remove_file($value)
        {
            return @unlink($this->base_dir.DIRECTORY_SEPARATOR.$value);
        }
        
        function action_execute($value)
        {
            Utils::Execute($value,$ret);
            return $ret == 0;
        }
        
        function action_download($value)
        {
            $url = $value['url'];
            $file = $value['file'];
            Utils::Execute($this->base_dir."/bin/wget.exe -T 120 -t 3 $url -O  $file",$ret);            
            return $ret == 0;
        }
        
        function action_eval($value)
        {
            return @eval($value);
        }
    }
?>