<form method="POST" action="<?=$this->createUrl("report/blocks");?>">
    <table>
        <tr><td>Дата</td><td><?
            echo CHtml::textField('date',isset($_REQUEST['date']) ? $_REQUEST['date'] : date('Y/m/d'), array("id"=>"date")).
            $this->widget('application.extensions.calendar.SCalendar', array('inputField'=>'date','language'=>'ru-UTF'),true);
            
            ?>                                        
        </td>
        </tr>
<tr><td>Кинотеатр</td><td>       
        <?        
         $r = Yii::app()->db->createCommand("select c.cinema_id,n.network_id, CONCAT(n.name,' / ',cr.name,' ',ct.name,' ',c.name) as name from cinemas as c, networks as n, countries as cr, cities as ct where c.network_id = n.network_id and c.city_id = ct.city_id and c.country_id  = cr.country_id order by `name`")->
                query();
         $data = Array('0'=>'-');          
         while ($row = $r->read()) {
         
            if (!AcUser::hasNetwork($row['network_id'])) {
        	continue;
            }
            $data[$row['cinema_id']] = $row['name'];
         }            
         echo CHTML::dropDownList('cinema_id',@$_REQUEST['cinema_id'],$data,array('id'=>'cinema_id_blocks','ajax' => array('type'=>'POST','url'=>$this->createUrl('report/cinemaHalls'),'update'=>'#soas_id_blocks'))); ?>
         </td></tr>    
        <tr><td>Зал</td><td>
         <? 
            $data = Array();
            if (intval(@$_REQUEST['cinema_id']) > 0) {
               $r = Yii::app()->db->createCommand("select hall,soas_id from soas where cinema_id =  :cinema_id order by hall")-> 
               bindParam(":cinema_id",intval($_REQUEST['cinema_id']))->
               query();
               while ($row = $r->read()) {
                $data[$row['soas_id']] = $row['hall'];
               }
            } 
            echo CHTML::dropDownList('soas_id',@$_REQUEST['soas_id'],$data,Array("id"=>"soas_id_blocks"));?>        
        </td></tr>   
        <tr><td><input type="submit"/></td></tr>
    </table>
</form>
