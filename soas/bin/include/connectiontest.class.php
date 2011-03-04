<?

class ConnectionTest
{
    private $base_path;
    private $conn_test_url;
    private $connection_name;
    private $unique_id;
    private $ids;
    
    function ConnectionTest($base_path, $conn_test_url, $connection_name, $unique_id, $ids)
    {
        $this->conn_test_url = $conn_test_url;
        $this->base_path = $base_path;
        $this->connection_name = $connection_name;  
        $this->unique_id = $unique_id;
        $this->ids = $ids;
    }
    
    function CheckConnection()
    {
        $timestamp = mktime();
        Utils::Dbg("Checking for internet connection: $this->conn_test_url");
        if ($data = Utils::RequestJSON($this->conn_test_url, array("timestamp" => $timestamp,"unique_id"=>$this->unique_id,"ids"=>implode(",",$this->ids))))
        {            
            if ($data->result == "success" && $data->timestamp == $timestamp)
            {   
                Utils::Dbg("Connection exists");
                return true;
            }
        }                
        Utils::Dbg("There is no internet connection");
        $this->connect();
        return false;
    }
    
    private function connect()
    {
        Utils::Dbg("Trying to connect");
        Utils::Execute("rasdial ".$this->connection_name." /disconnect");
        Utils::Execute("rasdial ".$this->connection_name);                        
    }
}


?>