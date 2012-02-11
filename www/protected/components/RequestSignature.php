<?
    class RequestSignature
    {
        function Validate($obj)
        {
            if (! isset($_REQUEST['sign'])) return false;
            
            
            $r = Array();
            foreach ($_REQUEST as $key=>$value)
            {
                if ($key == "PHPSESSID") continue;
                if ($key == "sign") continue;
                if ($value == $obj->route) continue;
                $r[$key] = $value;
            }
            
            ksort($r);
            
            $query = "";
            foreach ($r as $key=>$value)
            {
                $query.="$key=$value&";
            }            
            $query = rtrim($query, '&');
            return  sha1($query.'_'.Yii::app()->params['secret_key']) == $_REQUEST['sign'];
        }   
        
        function init() {}
    }


?>