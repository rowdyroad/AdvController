<?php
ini_set('memory_limit','256M');
class EventController extends Controller
{
    public function accessRules()
    {
        $data = parent::accessRules();
        $data[0]['actions'][] = 'filtered';
        $data[0]['actions'][] = 'idents';
        $data[0]['actions'][] = 'days';
        
        return $data;
    }

    function getCriteria()
    {
        $cr = new CDbCriteria(array('order'=>'dt_registered DESC'));
        
        if (isset($_REQUEST['EventSearch']))
        {
           $data = $_REQUEST['EventSearch'];

           if (!empty($data['dt_registered_begin']))
                $cr->addCondition("dt_registered>='{$data['dt_registered_begin']}'");

             if (!empty($data['dt_registered_end']))
                $cr->addCondition("dt_registered<='{$data['dt_registered_end']}'");

           if (!empty($data['promo_id']))
                $cr->addInCondition('promo_id',$data['promo_id']);

           if (!empty($data['client_id']))
               $cr->addInCondition('client_id',$data['client_id']);

           if (!empty($data['network_id']))
               $cr->addInCondition('network_id',$data['network_id']);

           if (!empty($data['cinema_id']))
               $cr->addInCondition('cinema_id',$data['cinema_id']);

           if (!empty($data['soas_id']))
               $cr->addInCondition('soas_id',$data['soas_id']);

           if (!empty($data['country_id']))
               $cr->addInCondition('country_id',$data['country_id']);

           if (!empty($data['city_id']))
               $cr->addInCondition('city_id',$data['city_id']);
        }
        return $cr;
    }
    
    
    function getEvents($model)
    {
	   $cr = $this->getCriteria();
       if (isset($_REQUEST['EventSearch']['excel']))
       { 
           $data = $_REQUEST['EventSearch'];
           $c = Array();

           if (!empty($data['dt_registered_begin']))
                 $c[] = "and dt_registered>='".str_replace("/","-",$data['dt_registered_begin'])."' ";

            if (!empty($data['dt_registered_end']))
                $c[] = "and dt_registered<='".str_replace("/","-",$data['dt_registered_end'])."' ";

           if (!empty($data['promo_id']))
                $c[] = "and promo_id in ('". implode("', '",$data['promo_id'])."') ";

           if (!empty($data['client_id']))
              $c[] = "and client_id in ('".implode("', '", $data['client_id'])."') ";

           if (!empty($data['network_id']))
               $c[] = "and network_id in ('".implode("', '", $data['network_id'])."') ";

           if (!empty($data['cinema_id']))
               $c[] = "and cinema_id in ('".implode("', '", $data['cinema_id'])."') ";

           if (!empty($data['soas_id']))
               $c[] = "and soas_id in ('".implode("', '", $data['soas_id'])."') ";

           if (!empty($data['country_id']))
                 $c[] = "and country_id in ('".implode("', '", $data['country_id'])."') ";
 
           if (!empty($data['city_id']))
               $c[] = "and city_id in ('".implode("', '", $data['city_id'])."') ";
 

               $query = "select
                                                    dt_registered,
                                                    promo_name,
                                                    hall,
                                                    cinema,
                                                    location,
                                                    client_name from events 
							where   
                                                    1 = 1 ".implode(" ",$c).
                                                    "order by dt_registered desc              
                                                   ";

				`rm *.csv`;

				$r = Yii::app()->db->createCommand($query)->query();
				

				$data = "Время регистрации;Ролик;Зал;Кинотеатр;Местоположение;Клиент;\r\n";                          
                foreach ($r as $row) {
					$data .= implode(";",array_values($row))."\r\n";
                }
				$data = iconv("UTF-8","WINDOWS-1251",$data);
				$filename = "Отчет_от_".date("Y-m-d_H_i").".csv";
				file_put_contents($filename, $data);
				header("location: $filename");
				die();
               
                
              /* $phpExcelPath = Yii::getPathOfAlias('ext.phpexcel');
               spl_autoload_unregister(array('YiiBase','autoload'));
               include($phpExcelPath . DIRECTORY_SEPARATOR . 'PHPExcel.php');
               $objPHPExcel = new PHPExcel();
               spl_autoload_register(array('YiiBase','autoload'));

               $d =  IdentEvent::model()->findAll($cr);
               
               $cacheMethod = PHPExcel_CachedObjectStorageFactory:: cache_to_phpTemp;
               $cacheSettings = array( ' memoryCacheSize ' => '1024MB');
               PHPExcel_Settings::setCacheStorageMethod($cacheMethod, $cacheSettings);
               if (!PHPExcel_Settings::setCacheStorageMethod($cacheMethod,$cacheSettings)) die('CACHEING ERROR');

               $a = $objPHPExcel->setActiveSheetIndex(0);
                $a->setCellValue('A1',"Время регистрации")
                      ->setCellValue('B1', "Ролик")
                      ->setCellValue('C1', "Зал")
                      ->setCellValue('D1', "Кинотеатр")
                      ->setCellValue('E1', "Местоположение")
                      ->setCellValue('F1', "Клиент");

                      $styleArray = array('font' => array('bold' => true));
                       $a->getStyle('A1')->applyFromArray($styleArray);
                        $a->getStyle('B1')->applyFromArray($styleArray);
                        $a->getStyle('C1')->applyFromArray($styleArray);
                        $a->getStyle('D1')->applyFromArray($styleArray);
                        $a->getStyle('E1')->applyFromArray($styleArray);
                        $a->getStyle('F1')->applyFromArray($styleArray);

               $i = 2;
                foreach ($d as $obj)
                {
                    $a->setCellValue('A'.$i,$obj->dt_registered)
                      ->setCellValue('B'.$i, $obj->promo_name)
                      ->setCellValue('C'.$i, $obj->hall)
                      ->setCellValue('D'.$i, $obj->cinema)
                      ->setCellValue('E'.$i, $obj->location)
                      ->setCellValue('F'.$i, $obj->client_name);
                    ++$i;
                }


                    $a->getColumnDimension('A')->setAutoSize(true);
                     $a->getColumnDimension('B')->setAutoSize(true);
                     $a->getColumnDimension('C')->setAutoSize(true);
                     $a->getColumnDimension('D')->setAutoSize(true);
                     $a->getColumnDimension('E')->setAutoSize(true);
                     $a->getColumnDimension('F')->setAutoSize(true);

                $objWriter = PHPExcel_IOFactory::createWriter($objPHPExcel, 'Excel2007');
                header('Content-Type: application/vnd.ms-excel');
                header('Content-Disposition: attachment;filename="SOAS_Report.xlsx"');
                header('Cache-Control: max-age=0');
                $objWriter->save('php://output');
                return;*/
       }
        $this->render('index',array('model'=>$model,'criteria'=>$cr));
    }
    
    function actionDays()
    {
        $cinema_id = (isset($_REQUEST['cinema_id'])) ? intval($_REQUEST['cinema_id']) : 0;
        $soas_id = (isset($_REQUEST['soas_id'])) ? intval($_REQUEST['soas_id']) : 0;
        $date_begin = (isset($_REQUEST['date_begin'])) ? $_REQUEST['date_begin'] : date('Y-m-d',mktime(0,0,0,date('m'),date('d')-10));
        $date_end = (isset($_REQUEST['date_end'])) ? $_REQUEST['date_end'] : date('Y-m-d');
        if ($soas_id) {
            $data = Yii::app()->db->createCommand("select s.*, unix_timestamp(s.`date`) as `timestamp`, p.name as promo_name, z.hall from soas_statistic  as s, promo_idents as p, soas as z where s.soas_id = :soas_id and `date` >= :date_begin and `date` < :date_end and s.promo_id = p.id and z.soas_id = s.soas_id  order  by `hall`,`date`")
            ->bindParam(":soas_id",$soas_id)
            ->bindParam(":date_begin",$date_begin)
            ->bindParam(":date_end",$date_end)
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
            $data = Yii::app()->db->createCommand("select s.*, unix_timestamp(s.`date`) as `timestamp`,p.name as promo_name, z.hall  from soas_statistic as s, promo_idents as p,  soas as z  where z.cinema_id =:cinema_id and s.soas_id = z.soas_id and `date` >= :date_begin and `date` < :date_end and s.promo_id = p.id  order by `hall`,`date`")
            ->bindParam(":cinema_id",$cinema_id)
            ->bindParam(":date_begin",$date_begin)
            ->bindParam(":date_end",$date_end)
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
    
    function actionIndex()
    {
	$this->getEvents(new Event());
    }

    function actionFiltered()
    {
	$this->getEvents(new FilteredEvent());
    }
    
    function actionIdents()
    {
	$this->getEvents(new IdentEvent());
    }
}
?>