<?

class PromoSynchronize
{
    private $soas_id;
    private $base_path;
    private $run_dir;
    private $etc_dir;
    private $promos_dir;
    private $promos_sync_url;
    private $promos_url;

    public function PromoSynchronize($soas_id, $base_path, $promos_sync_url, $promos_url)
    {
        $this->soas_id = $soas_id;
        $this->base_path = $base_path;
        $this->run_dir = $base_path . "\\run";
        $this->etc_dir = $base_path . "\\etc";
        $this->promos_dir = $base_path . "\\data";
        $this->promos_sync_url = $promos_sync_url;
        $this->promos_url = $promos_url;
    }

    function Process()
    {
        Utils::Dbg("Synchronize for $this->soas_id");
        if (($sync = $this->getSyncData($this->soas_id)) === false)
        {
            Utils::Dbg("Couldn't receive synchronize data. Wait for next time.");
            return false;
        }

        $exists = $this->getExistsPromos();
        foreach ($exists as $id)
        {
            if (!isset($sync[$id]))
            {
                $this->removePromo($id);
            }
        }
        foreach ($sync as $id => $sha1)
        {
            if (!in_array($id, $exists))
            {
                $this->downloadPromo($id, $sha1);
            }
            else
            {
                if ($this->getSha1($id) != $sha1)
                {
                    Utils::Dbg("Corrupted file $id promo");
                    $this->downloadPromo($id, $sha1);
                }
            }
        }
    }

    private function getSha1($promo_id)
    {
	return sha1_file("$this->promos_dir/$this->soas_id/$promo_id");
    }

    private function getExistsPromos()
    {
        Utils::Dbg("Getting exists promos from  $this->promos_dir for $this->soas_id");
        $data = Utils::Execute("dir /B $this->promos_dir\\$this->soas_id");
        $data = preg_split("/\r\n/", $data, -1, PREG_SPLIT_NO_EMPTY);
        Utils::Dbg("Existing promos: " . implode(",", $data));
        return $data;
    }

    private function getSyncData()
    {
        Utils::Dbg("Getting synchronize data from  $this->promos_sync_url for $this->soas_id");
        $req = Utils::Request($this->promos_sync_url, array("soas_id" => $this->soas_id), $ret);

        if ($ret != 0)
        {
            Utils::Dbg("Incorrect request process $ret");
            Utils::Dbg($data);
            return false;
        }

        $data = json_decode($req);

        if (!is_object($data))
        {
            Utils::Dbg("Incorrect received data");
            Utils::Dbg($req);
            return false;
        }

        if ($data->result == "error")
        {
            Utils::Dbg("Error: " . $data->errorMessage);
            return false;
        }
        $data = $data->data;

        $dbg = "";
        $ret = array();        
        foreach ($data as $promo)
        {
            $ret[$promo->id] = $promo->sha1;
            $dbg .= "$promo->id,";
        }
        
        if (empty($dbg))
        {
            Utils::Dbg("Nothing to receive");
            return false;
        }
        
        Utils::Dbg("Sync Received: " . rtrim($dbg, ","));
        return $ret;
    }

    private function removePromo($id)
    {
        Utils::Dbg("Remove old promo $id from  $this->promos_dir/$this->soas_id");
        if (file_exists("$this->promos_dir/$this->soas_id/$id"))
        {
            unlink("$this->promos_dir/$this->soas_id/$id");
        }
    }

    private function downloadPromo($id, $sha1)
    {
        Utils::Dbg("Downloading promo $id from  $this->promos_url for $this->soas_id");
        @mkdir("$this->promos_dir/$this->soas_id");
        Utils::Execute($this->base_path."/bin/wget.exe -T 120 -t 3 $this->promos_url/$id -O  $this->promos_dir/$this->soas_id/$id");
        if ($this->getSha1($id) != $sha1)
        {
            Utils::Dbg("Incorrect checksum for $id promo");
            unlink("$this->promos_dir/$this->soas_id/$id");
        }
    }
}

?>
