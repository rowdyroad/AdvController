<script>
    function setHalls(id,cinema_id) {
        var z = document.getElementById(id); 
        z.options = [];
        $.getJSON('<?=$this->createUrl('report/cinamehalls');?>',{cinema_id:cinema_id},function(data) {
           
           if (data.result == "success") {
            for (var i =0; i < data.data.length; ++i) {
                z.options[i] = new Option(data.data[i].hall, data.data[i].soas_id); 
            }                           
           } 
        });
    }
</script>
<form method="POST" action="<?=$this->createUrl("report/days");?>">
    <table>
        <tr><td>Период отчета</td><td><?
            echo CHtml::textField('date_begin',
                                isset($_REQUEST['date_begin']) ? $_REQUEST['date_begin'] : date('Y/m/d',mktime(0,0,0,date('m')-1)),
                                array("id"=>"date_begin")).
            $this->widget('application.extensions.calendar.SCalendar',
                                array('inputField'=>'date_begin','language'=>'ru-UTF'),true);                                                                                                
            ?><select name="period">
                <option value="month" <? if (@$_REQUEST['period']=="month") { ?>selected<? }; ?>>Месяц</option>
                <option value="week" <? if (@$_REQUEST['period']== "week") { ?>selected<? }; ?> >Неделя</option>
                <? for ($i = 1;$i < 31; ++$i) {
                    ?><option value="<?=$i;?>" <? if (intval(@$_REQUEST['period'])== $i) { ?>selected<? }; ?>><?=$i;?> дня(ей)</option><?
                } ?>                
            </select>                                        
        </td></tr>
        <tr><td>Кинотеатр</td><td>       
        <?        
         $r = Yii::app()->db->createCommand("select c.cinema_id, CONCAT(n.name,' / ',cr.name,' ',ct.name,' ',c.name) as name from cinemas as c, networks as n, countries as cr, cities as ct where c.network_id = n.network_id and c.city_id = ct.city_id and c.country_id  = cr.country_id order by `name`")->
                query();
         $data = Array('0'=>'-');                   
         while ($row = $r->read()) {
            $data[$row['cinema_id']] = $row['name'];
         }            
         echo CHTML::dropDownList('cinema_id',@$_REQUEST['cinema_id'],$data,array('id'=>'cinema_id_days','ajax' => array('type'=>'POST','url'=>$this->createUrl('report/cinemaHalls',array('undefined'=>1)),'update'=>'#soas_id_days'))); ?>
         </td></tr>    
        <tr><td>Зал</td><td>
                
        <? 
            $data = Array("0"=>'-');
            if (intval(@$_REQUEST['cinema_id']) > 0) {
               $r = Yii::app()->db->createCommand("select hall,soas_id from soas where cinema_id =  :cinema_id order by hall")-> 
               bindParam(":cinema_id",intval($_REQUEST['cinema_id']))->
               query();
               while ($row = $r->read()) {
                $data[$row['soas_id']] = $row['hall'];
               }
            }
            echo CHTML::dropDownList('soas_id',@$_REQUEST['soas_id'],$data,Array('id'=>'soas_id_days'));
            
        ?>        
        </td></tr>   
        <tr><td><input type="submit"/></td></tr>
    </table>
</form>
