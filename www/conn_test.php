<?
    error_reporting(0);
    
    file_put_contents("conn_test.log",sprintf("[%s] %s",date("YYYY-mm-dd H:i:s"),print_r($_REQUEST,true)));
    $ids = preg_split("/\|/",$_REQUEST['ids']);    
    if (!empty($ids))
    {    
        mysql_connect("localhost","soas","1q2w3e4r");
        mysql_select_db('soas');
        foreach($ids as $id)
        {    
            mysql_query("update `soas` set `last_heart_beat` = NOW() where `soas_id` = '$id'"); 
        }        
        mysql_close();
    }    
    echo json_encode(Array("result"=>"success","timestamp"=>intval($_REQUEST["timestamp"])));
?>
