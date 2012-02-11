<?php

/*
insert into events (
event_id,
dt_registered,
inserted_time,
probability,
hall,
soas_id,
network_id,
cinema_id,
cinema,
country_id,
city_id,
location,
client_id,
client_name,
promo_ident,
promo_name 
)
select 
`e`.`event_id`,
`e`.`dt_registered` AS `dt_registered`,
`e`.`inserted_time` AS `inserted_time`,                                                    
`e`.`probability` AS `probability`,
`e`.`hall` AS `hall`,
`e`.`soas_id` AS `soas_id`,
`e`.`network_id` AS `network_id`,
`e`.`cinema_id` AS `cinema_id`,
`e`.`cinema` AS `cinema`,
`e`.`country_id` AS `country_id`,
`e`.`city_id` AS `city_id`,
`e`.`location` AS `location`,
NULL  as `client_id`,
`e`.`client_name`,
`e`.`promo_ident`,
`e`.`promo_name` 
from v_idents_events as e
*/

class RemotingController extends CController
{

    public function RemotingController()
    {
        //file_put_contents("remote.log",print_r($_REQUEST,true),FILE_APPEND);
    }

    public function q($query)
    {
        return Yii::app()->db->createCommand($query);
    }

    public function actionSynchronize()
    {
        $response = array();
        try
        {
            if (!Yii::app()->signature->Validate($this))
            {
                //   throw new Exception("Incorrect signature");
            }

            if (!isset($_REQUEST['soas_id']))
            {
                throw new Exception("Undefined soas id");
            }

            $soas_id = $_REQUEST['soas_id'];

            $this->q("update `soas` set `last_heart_beat` = NOW() where `soas_id` = '$soas_id'")->
                execute();
            $remote_addr = $_SERVER['REMOTE_ADDR'];

            $this->q("insert into `connections_log` (`soas_id`,`remote_addr`) values ('$soas_id', '$remote_addr')")->
                execute();

            $response['result'] = 'success';
            $response['data'] = $this->q("select lnk_soas_promo_id as id, sha1 from lnk_soas_promo where soas_id='$soas_id'")->
                queryAll();
        }
        catch (exception $e)
        {
            Yii::log($e->getMessage(), 'error');
            $response['result'] = 'error';
            $response['errorMessage'] = $e->getMessage();
        }

        echo CJSON::encode($response);
    }


    public function actionIdents()
    {
        echo CJSON::encode(array("result" => "success", "data" => $this->q("select * from promo_idents where actived = 1")->queryAll()));
    }

    public function actionGet($length)
    {
        $count = $this->q("select count(*) from promo_idents where actived = 1")->queryScalar();
        if ($count >= 253)
        {
            echo CJSON::encode(array("result" => "error", "errno" => 1, "msg" => "no free ident"));
            return;
        }

        while (true)
        {
            $i = rand(1, 255);
            if ($i == 15)
                continue;
            $r = $this->q("select 1 from promo_idents where ident = :ident and actived = 1")->
            bindParam(":ident",$i)->
            queryAll();            
            if (empty($r))
            {
                echo CJSON::encode(array("result" => "success", "ident" => $i));
                return;
            }
        }
    }


    public function actionRename($id, $name)
    {
        $this->q("update promo_idents set name = :name  where id = :id")->
        bindParam(":name", addslashes($name))->
        bindParam(":id",intval($id))->
        execute();
        echo CJSON::encode(array("result" => "success"));
    }

    public function actionAdd($ident, $length, $name)
    {
        $this->q("insert into promo_idents (`ident`,`length`,`name`) values (:ident, :length, :name)")->
        bindParam(":ident",intval($ident))->
        bindParam(":length",intval($length))->
        bindParam(":name",addslashes($name))->
        execute();
        echo CJSON::encode(array("result" => "success", "id" => Yii::app()->db->getLastInsertID()));
    }

    public function actionDelete($id)
    {
        $this->q("delete from promo_idents where id = :id")->
        bindParam(":id",intval($id))->
        execute();
        echo CJSON::encode(array("result" => "success"));
    }

    public function actionRegistration()
    {
        $response = array();
        try
        {
            if (!Yii::app()->signature->Validate($this))
            {
                throw new Exception("Incorrect signature");
            }

            $soas = new Soas;
            $soas->attributes = $_REQUEST;
            $soas->save();
            $response = array("result" => "success", "soas_id" => $soas->soas_id);
        }
        catch (exception $e)
        {
            Yii::log($e->getMessage(), 'error');
            $response['result'] = 'error';
            $response['errorMessage'] = $e->getMessage();
        }

        echo CJSON::encode($response);
    }

    public function actionCache()
    {
        try
        {            
            Yii::log("Caching action started",'trace');
	        $this->q("update events_cache set status = 3 where exists (select 1 from events where event_id = events_cache.event_cache_id)")->execute();
            $count = $this->q("update events_cache set status = 2 where status = 1 limit 100")->execute();
            Yii::log("Caching $count items..",'trace');
            if ($count > 0) {
                $this->q("insert into events (
                                        event_id,
                                        dt_registered,
                                        probability,
                                        hall,
                                        soas_id,
                                        network_id,
                                        cinema_id,
                                        cinema,
                                        country_id,
                                        city_id,
                                        location,
                                        client_id,
                                        client_name,
                                        promo_name,
                                        promo_id
                )
                select
                                `e`.`event_cache_id`,
                                `e`.`dt_registered` AS `dt_registered`,                                                   
                                `e`.`probability` AS `probability`,
                                `s`.`hall` AS `hall`,
                                `s`.`soas_id` AS `soas_id`,
                                `s`.`network_id` AS `network_id`,
                                `s`.`cinema_id` AS `cinema_id`,
                                `s`.`cinema` AS `cinema`,
                                `s`.`country_id` AS `country_id`,
                                `s`.`city_id` AS `city_id`,
                                `s`.`location` AS `location`,
                                `clients`.`client_id`,
                                `clients`.`name` AS `client_name`,
                                `p`.`name` AS `promo_name`,
                                `p`.`id` as `promo_id`
                from 
                ((`events_cache` `e` join `v_soas` `s`) 
                join 
                (`promo_idents` `p` left join `clients` on((`p`.`client_id` = `clients`.`client_id`)))) 
                where 
                ((`e`.`status` = 2) and (`e`.`soas_id` = `s`.`soas_id`) and (`e`.`promo_id` = `p`.`id`))")->
                    execute();
        
        
                $r = $this->q("select soas_id, promo_id, unix_timestamp(dt_registered) as dt from events_cache where status = 2")->
                    query();
        
                while ($row = $r->read())
                {
                    $timestamp = intval($row['dt']);
                    $soas_id = intval($row['soas_id']);
                    $promo_id = intval($row['promo_id']);
                    $hour = intval(date('H', $timestamp));
                    $date = date('Y-m-d', ($hour < 3) ? $timestamp - 24 * 3600 : $timestamp);
                    $this->q("INSERT INTO soas_statistic (soas_id,promo_id,`date`) VALUES (:soas_id,:promo_id,:date) ON DUPLICATE KEY UPDATE `count`=`count`+1")->
                        bindParam(":soas_id", $soas_id)->
                        bindParam(":promo_id", $promo_id)->
                        bindParam(":date",$date)->
                        execute();
                        
                    if ($cinema_id = $this->q("select cinema_id from soas where soas_id = :soas_id")->
                        bindParam(":soas_id", $soas_id)->
                        queryScalar())
                    {
                        $this->q("INSERT INTO cinema_statistic (cinema_id, promo_id,`date`) VALUES (:cinema_id,:promo_id,:date) ON DUPLICATE KEY UPDATE `count`=`count`+1")->
                            bindParam(":cinema_id", $cinema_id)->
                            bindParam(":promo_id", $promo_id)->
                            bindParam(":date", $date)->
                            execute();
                    }
                }
        
                $this->q("update events_cache set status = 3 where status = 2")->execute();
            }
            echo CJSON::encode(array("result"=>"success","count"=>$count));           
        }catch (exception $e) {
             Yii::log($e->getMessage(), 'error');
            echo CJSON::encode(array("result"=>"error","errorMessage"=>$e->getMessage()));           
        }         
    }

    private function addEventToCache($event_id, $soas_id, $promo_ident, $prob, $timestamp)
    {
        if (!$r = $this->q("select id, length from promo_idents where ident = :ident and actived = 1")->
            bindParam(":ident", $promo_ident)->
            queryRow())
        {
            return;
        }

        $promo_id = $r['id'];
        $length = $r['length'];
        $dt_registered = date('Y-m-d H:i:s', $timestamp);

        if ($r = $this->q("select * from events_cache where 
                                    soas_id = :soas_id 
                                    and
                                    status = 0
                                    and
                                    unix_timestamp(dt_registered) + promo_length > :timestamp ")->
            bindParam(":soas_id", $soas_id)->
            bindParam(":timestamp", $timestamp)->
            queryRow())
        {
            if ($prob > $r['probability'])
            {
                $this->q("update events_cache set 
                            event_cache_id = :new_event_id,
                            promo_id = :promo_id,
                            dt_registered = :dt_registered,
                            promo_length = :promo_length,
                            probability = :probability 
                          where 
                            event_cache_id = :event_id")->
                  bindParam(":new_event_id",$event_id)->
                  bindParam(":promo_id", $promo_id)->
                  bindParam(":dt_registered", $dt_registered)->
                  bindParam(":probability", $prob)->
                  bindParam(":promo_length", $length)->
                  bindParam(":event_id", $r['event_cache_id'])->execute();
            }
            return;
        }

        $this->q("update events_cache set
                            status = 1
                        where 
                            soas_id = :soas_id
                            and 
                            status = 0")->
            bindParam(":soas_id", $soas_id)->
            execute();

        $this->q("insert into  events_cache (
                                        event_cache_id,
                                        promo_id,
                                        soas_id,
                                        dt_registered,
                                        promo_length,
                                        probability)
                            values( 
                                        :event_id, 
                                        :promo_id,
                                        :soas_id, 
                                        :dt_registered, 
                                        :promo_length, 
                                        :probability)")->
        bindParam(":event_id",$event_id)->
        bindParam(":promo_id", $promo_id)->
        bindParam(":soas_id", $soas_id)->        
        bindParam(":dt_registered", $dt_registered)->
        bindParam(":probability", $prob)->
        bindParam(":promo_length", $length)->
        execute();
    }

    public function actionAddIdentEvent()
    {
        $response = array();
        $transaction = null;

        try
        {
            if (!isset($_REQUEST['soas_id']))
            {
                throw new Exception("Undefined soas id");
            }

            if (!Yii::app()->signature->Validate($this))
            {
                // throw new Exception("Incorrect signature");
            }

            $eventSoasId = intval($_REQUEST['soas_id']);

            $eventCount = preg_match_all('/(\d+),(\d+)(,(\d+))?/', $_REQUEST['events'], $matches);
            if ($eventCount === false)
                throw new Exception('Error parsing input string');

            if ($eventCount === 0)
                throw new Exception('No events were provided');
                
            $transaction = Yii::app()->db->beginTransaction();
            for ($i = 0; $i < $eventCount; ++$i)
            {
                $eventUnixTimeStamp = intval($matches[1][$i]);
                $eventPromoId = intval($matches[2][$i]);
                $eventModel = new PromoRegistrationIdentEvent;
                
                $date =  date('Y-m-d H:i:s', $eventUnixTimeStamp);                
                if ($date[0] == '3') { $date[0] = '2'; }
                $eventModel->dt_registered = $date;
                $eventModel->soas_id = $eventSoasId;
                $eventModel->promo_ident = $eventPromoId;
                $eventModel->probability = (!empty($matches[4][$i])) ? intval($matches[4][$i]) :
                    100;
                    
        	try
        	{

                    if ($eventModel->save())
                    {
                        $this->addEventToCache($eventModel->promo_idents_event_id, $eventSoasId, $eventPromoId, $eventModel->probability, $eventUnixTimeStamp);
                    }
                    else
                    {
                        throw new Exception("CModel::save return false");
                    }
                    
                }
                catch (CDbException $e)
                {
            	    if ($e->errorInfo[1] == 1062)
            	    {
            		Yii::log($e->getMessage(),'warning');
            	    } else {
            		throw $e;
            	    }
                }
             }

            $transaction->commit();
            $this->q("update `soas` set `last_heart_beat` = NOW() where `soas_id` = '$eventSoasId'")->
                execute();

            $response['result'] = 'success';
        }
        catch (exception $e)
        {
            Yii::log($e->getMessage(), 'error');
            if ($transaction !== null)
                $transaction->rollback();

            $response['result'] = 'error';
            $response['errorMessage'] = $e->getMessage();
        }

        echo CJSON::encode($response);
    }
}
