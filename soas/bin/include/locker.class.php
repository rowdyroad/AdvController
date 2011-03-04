<?

class Locker
{
    private $locker;
    private $handle;
    function Locker($prefix, $base_path)
    {
        $this->locker = "$base_path/run/$prefix.lock";
        file_put_contents($this->locker, "");
    }

    function Lock()
    {
        if ($this->handle = Utils::Lock($this->locker))
        {
            return true;
        }
        return false;
    }

    function TryLock()
    {
        if ($this->handle = Utils::TryLock($this->locker))
        {
            return true;
        }
        return false;
    }

    function Unlock()
    {
        Utils::Unlock($this->handle);
    }
}

?>