#!/usr/bin/php
<?

require_once ("include/config.php");
require_once ("include/utils.class.php");
require_once ("include/locker.class.php");

$soas_id = $argv[1];
$promo_id = $argv[2];
$timestamp = $argv[3];
$probability = $argv[4];

$locker = new Locker("events", BASE_DIR);

function lockedFileAppend($filename, $str)
{
    global $locker;
    if ($locker->Lock())
    {
        file_put_contents($filename, $str, FILE_APPEND);
        $locker->Unlock();
    }
}

lockedFileAppend(RUN_DIR."/archive.arh", sprintf("[%s] %s event %s %s %s\n", date("Y-m-d H:i:s"),$timestamp, $soas_id, $promo_id,$probability));
lockedFileAppend(RUN_DIR."/events/$soas_id", sprintf("%s,%s,%s|", $timestamp, $promo_id,$probability));
?>