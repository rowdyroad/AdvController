<?

 class SOASRegistration
 {
        
    function Process($data)
    {
        Utils::Dbg("Registration {$data['channel']} channel");
        $d = Utils::RequestJSON(REGISTRATION_URL, $data);
        if ($d->result == "success")
        {
            $data['soas_id'] = $d->soas_id;
            createIniFile(ETC_DIR . "/{$data['channel']}.ini", $data);
            $streamer = readIniFile(ETC_DIR . "/streamer.ini");
            $streamer["{$data['channel']}_key"] = $d->soas_id;
            createIniFile(ETC_DIR . "/streamer.ini", $streamer);
            execute("killall java");
            return $d->soas_id;
        }
        else
        {
            Utils::Dbg("Error:" . $d->error);
            return false;
        }
    }
    
 }

?>