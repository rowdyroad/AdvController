<?
require_once ("include/utils.class.php");
require_once ("include/connectiontest.class.php");

$config = Utils::Config();

while (true)
{
        
    $checker = new ConnectionTest(  
                                    $config->general->base_dir, 
                                    $config->general->host.$config->synchronizer->connection_test_url, 
                                    $config->general->connection_name,
                                    Utils::GetUniqueID(),
                                    $config->general->ids
                                    );
    if ($checker->CheckConnection())
    {
        require_once ("include/promosynchronize.class.php");
        require_once ("include/eventsynchronize.class.php");
        
        foreach ($config->general->ids as $id)
        {
            Utils::Dbg("Synchronize for $id");
            $promo_synchronize = new PromoSynchronize(
                                                        $id, 
                                                        $config->general->base_dir, 
                                                        $config->general->host.$config->synchronizer->promos_sync_url,
                                                        $config->general->host.$config->synchronizer->promos_url
                                                        );
            $promo_synchronize->Process();
            
                        
            $event_synchronize = new EventSynchronize(
                                                        $id, 
                                                        $config->general->base_dir,
                                                        $config->general->host.$config->synchronizer->events_submit_url
                                                    );        
            $event_synchronize->Process();
        }   
    }    
    sleep($config->synchronizer->period);
}

?>