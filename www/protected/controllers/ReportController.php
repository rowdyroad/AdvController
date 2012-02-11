<?
class ReportController extends Controller
{
    public function accessRules()
    {
        $data = parent::accessRules();
        $data[0]['actions'][] = 'days';
        $data[0]['actions'][] = 'blocks';       
        $data[0]['actions'][] = 'cinemahalls';       
        return $data;
    }
    
    private function getDaysReport($date_begin,$date_end,$cinema_id,$soas_id = 0)
    {
          if ($soas_id) {
            $query = "select s.*, unix_timestamp(s.`date`) as `timestamp`, p.name as promo_name, z.hall from soas_statistic  as s, promo_idents as p, soas as z where s.soas_id = :soas_id and `date` >= :date_begin and `date` < :date_end and s.promo_id = p.id and z.soas_id = s.soas_id  order  by `hall`,`date`";

            if (!Yii::app()->user->is_admin) {
            
        	if ($fake_soas_id = Yii::app()->db->createCommand("select fake_soas_id from fakes where soas_id = :soas_id")->bindParam(":soas_id",$soas_id)->queryScalar()) {
            
                $query = "select z.soas_id, s.`date`, s.`promo_id`,s.`count`,
            		unix_timestamp(s.`date`) as `timestamp`, 
            		p.name as promo_name, 
            		z.hall 
            		from soas_statistic as s, 
            		promo_idents as p, 
            		soas as z 
            		where 
            		s.soas_id = $fake_soas_id 
            		and 
            		`date` >= :date_begin 
            		and `date` < :date_end 
            		and s.promo_id = p.id and z.soas_id = :soas_id  order  by `hall`,`date`";
            	}
            }

            $data = Yii::app()->db->createCommand($query)
            ->bindParam(":soas_id",$soas_id)
            ->bindParam(":date_begin",date('Y-m-d',$date_begin))
            ->bindParam(":date_end",date('Y-m-d',$date_end))
            ->query();
            
            $info = Yii::app()->db->createCommand("select   cinemas.name as cinema_name, 
                                                            networks.name as network_name 
                                                    from 
                                                            soas,
                                                            cinemas,
                                                            networks 
                                                    where 
                                                            soas.soas_id = :soas_id 
                                                        and 
                                                            cinemas.cinema_id = soas.cinema_id 
                                                        and 
                                                            networks.network_id = cinemas.network_id")
            ->bindParam(":soas_id",$soas_id)->queryRow();
                                    
        } elseif ($cinema_id) {
        
    	    $query = "select s.*, unix_timestamp(s.`date`) as `timestamp`,p.name as promo_name, z.hall  from soas_statistic as s, promo_idents as p,  soas as z  where z.cinema_id = :cinema_id and s.soas_id = z.soas_id and `date` >= :date_begin and `date` < :date_end and s.promo_id = p.id  order by `hall`,`date`";
    	    if (!Yii::app()->user->is_admin) {
    	    
    		$query = "select * from (
    					select 
    					t.soas_id, 
    					s.`date`,
    					s.promo_id,
    					s.`count`,
    					unix_timestamp(s.`date`) as `timestamp`,
    					p.name as promo_name, 
    					z.hall
			        		from 
			        		soas_statistic as s, 
			        		promo_idents as p,  
			        		soas as z,  
			        		(select f.fake_soas_id, f.soas_id from fakes as f,soas as s where s.cinema_id = :cinema_id and s.soas_id = f.soas_id) as t  
			        		where   
			        		z.cinema_id = :cinema_id 
			        		and s.soas_id = t.fake_soas_id  and 
			        		`date` >= :date_begin and 
			        		`date` <  :date_end  
			        		and s.promo_id = p.id and 
			        		t.soas_id = z.soas_id
			        	union
    		    		    select 
    		    			s.soas_id,
    		    			s.`date`,
    		    			s.promo_id,
    		    			s.`count`, 
    		    			unix_timestamp(s.`date`) as `timestamp`,
    		    			p.name as promo_name,
    		    			z.hall  
    		    			from 
    		    			soas_statistic as s, 
    		    			promo_idents as p,  
    		    			soas as z  
    		    			where  
    		    			z.cinema_id = :cinema_id 
    		    			and 
    		    			s.soas_id = z.soas_id 
    		    			and `date` >= :date_begin
    		    			 and `date` <  :date_end 
    		    			 and s.promo_id = p.id and not exists (select fake_soas_id from fakes where fakes.soas_id = s.soas_id)) as t order by `hall`,`date`";
    	    }
        
            $data = Yii::app()->db->createCommand($query)
            ->bindParam(":cinema_id",$cinema_id)
            ->bindParam(":date_begin",date('Y-m-d',$date_begin))
            ->bindParam(":date_end",date('Y-m-d',$date_end))
            ->query();
            
            $info = Yii::app()->db->createCommand("select   cinemas.name as cinema_name, 
                                                            networks.name as network_name 
                                                    from 
                                                          
                                                            cinemas,
                                                            networks 
                                                    where 
                                                            cinemas.cinema_id = :cinema_id
                                                        and 
                                                            networks.network_id = cinemas.network_id")
            ->bindParam(":cinema_id",$cinema_id)->queryRow();
        }
        
        $this->render('days',array("data"=>$data,'date_begin'=>$date_begin,'date_end'=>$date_end, 'info'=>$info));  
    }
    
    public function actionIndex()
    {
        $this->render('index');
    }

    public function actionCinemaHalls()
    {
        $r = Yii::app()->db->createCommand("select hall,soas_id from soas where cinema_id = :cinema_id order by hall")->
                                bindParam(":cinema_id",intval(@$_REQUEST['cinema_id']))->
                                query(); ;
        if (isset($_REQUEST['undefined'])) {
            echo CHtml::tag('option',array(),'-',true);
        }                                                                
        while($row = $r->read()){
            echo CHtml::tag('option',array('value'=>$row['soas_id']),CHtml::encode($row['hall']),true);
        }
    }
    
    private function getSoasId($cinema_id,$hall) {
        return Yii::app()->db->createCommand("select soas_id from soas where cinema_id = :cinema_id and hall = :hall")->
                bindParam(":cinema_id",$cinema_id)->
                bindParam(":hall",$hall)->
                queryScalar();        
    }
    
    public function actionDays()
    {
            $cinema_id = intval(@$_REQUEST['cinema_id']);
            $date_begin = strtotime(@$_REQUEST['date_begin']);
            
            switch ($_REQUEST['period']) {
                case 'month':
                    $date_end = mktime(0,0,0,date('m',$date_begin) + 1,date('d',$date_begin),date('Y',$date_begin));
                break;
                case 'week':
                    $date_end = mktime(0,0,0,date('m',$date_begin),date('d',$date_begin)+7,date('Y',$date_begin));
                break;
                default:
                    $date_end = mktime(0,0,0,date('m',$date_begin),date('d',$date_begin) + intval($_REQUEST['period']), date('Y',$date_begin));
            }            
            $soas_id = intval(@$_REQUEST['soas_id']);    
            $this->getDaysReport($date_begin,$date_end, $cinema_id,$soas_id);
    }    
    
    public function actionBlocks()
    {
            $cinema_id = intval(@$_REQUEST['cinema_id']);
            $soas_id = intval(@$_REQUEST['soas_id']);
            $date = strtotime(@$_REQUEST['date']);
            
            $stat_soas_id = $soas_id;
            if (!Yii::app()->user->is_admin) {
        	if ($fake_id = Yii::app()->db->createCommand("select fake_soas_id from fakes where soas_id = :soas_id")->bindParam(":soas_id",$soas_id)->queryScalar()) {
        	    $stat_soas_id = $fake_id;
        	}
            }
            
            $data = Yii::app()->db->createCommand("select events.*, unix_timestamp(dt_registered) as `timestamp` from events where 
            soas_id = :soas_id
            and
            ((DATE(`dt_registered`) = :date and HOUR(dt_registered) > 3)
             or (HOUR(`dt_registered`) < 3 and DATE(`dt_registered` - INTERVAL 1 DAY) = :date))
            order by `dt_registered` asc")->
            bindParam(":soas_id",$stat_soas_id)->
            bindParam(":date",date('Y-m-d',$date))->
            query();
            
              $info = Yii::app()->db->createCommand("select   cinemas.name as cinema_name, 
                                                            networks.name as network_name,
                                                            soas.hall,
                                                            :date as `timestamp`
                                                    from 
                                                            soas,
                                                            cinemas,
                                                            networks 
                                                    where 
                                                            soas.soas_id = :soas_id 
                                                        and 
                                                            cinemas.cinema_id = soas.cinema_id 
                                                        and 
                                                            networks.network_id = cinemas.network_id")->
            bindParam(":soas_id",$soas_id)->
            bindParam(":date",$date)->
            queryRow();
            
            $this->render('blocks',array('data'=>$data,'info'=>$info));
    }
}  
  
    
?>