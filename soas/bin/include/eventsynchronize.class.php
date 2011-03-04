<?

require_once ("locker.class.php");

class EventSynchronize
{
    private $filename;
    private $base_path;
    private $locker;
    private $soas_id;
    private $events_sync_url;

    function EventSynchronize($soas_id, $base_path, $events_sync_url)
    {
        $this->locker = new Locker(__class__, $base_path);
        $this->filename = "$base_path/run/events/$soas_id";
        $this->base_path = $base_path;
        $this->soas_id = $soas_id;
        $this->events_sync_url = $events_sync_url;
    }

    private function lockedRead()
    {
        if ($this->locker->Lock())
        {
            $data = file_get_contents($this->filename);
            $this->locker->Unlock();
            return $data;
        }
        return false;
    }

    private function lockedRemoveHead($head)
    {
        if ($this->locker->Lock())
        {
            $data = substr(file_get_contents($this->filename), strlen($head));
            file_put_contents($this->filename, $data);
            $this->locker->Unlock();
        }
        return false;
    }

    function Process()
    {
        Utils::Dbg("Start event synchronize");
        if (!is_file($this->filename))
        {
            Utils::Dbg("Incorrect file $this->filename. Stop process");
            return;
        }

        if (($events = $this->lockedRead()) !== false)
        {
            if (empty($events))
            {
                Utils::Dbg("Nothing to synchronize");
                return;
            }

            Utils::Dbg("Events for $this->soas_id: $events");
            Utils::Dbg("Register events: $events");
            $error = "Undefined";
		$data = Utils::RequestJSON($this->events_sync_url, array("events" => $events,"soas_id" => $this->soas_id));
            if ($data) 
            {
                if ($data->result == "success")
                {
                    $this->lockedRemoveHead($events);
                    Utils::Dbg("Event synchronize complete successfully");
                    return;
                }
                else
                {
                    $error = $data->errorMessage;
                }
            }
            ;
            Utils::Dbg("Error while events submiting ($error)");
        }
        else
        {
            Utils::Dbg("Couldn't event synchronize");
        }


    }
}

?>