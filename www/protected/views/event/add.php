    <script>
        function error(msg)
        {
            $("#status").html("Ошибка:" + msg).css("color","red");
        }
        function add()
        {
            var time = parseInt(new Date($("#date").val()).getTime() / 1000) 
                        + parseInt($("#hour").val() *  3600) 
                        + parseInt($("#minute").val() * 60) 
                        + parseInt($("#second").val());

            var soas_id = $("#soas_id").val();
            if (soas_id <= 0) {
                error('Неверный кинотеатр или зал');
                return;
            }
            var promo_ident = $("#promo_ident").val();
            $.getJSON('<?=$this->createUrl('remoting/addidentevent');?>', 
            { 
                soas_id: soas_id,
                events: time + ',' + promo_ident + ',100'
            }, function(data) {
                if (data.result == "error") {
                    error(data.errorMessage);
                } else {
                    $("#status").html("Ролик успешно добавлен").css("color","green");
                }
            });
        }
    </script>
    
    <form>
    <table>
        <td>
            <td colspan="2" id="status"></td>
        </td>
        <tr>            
            <td>Кинотеатр</td>
            <td>
            <?        
             $r = Yii::app()->db->createCommand("select c.cinema_id, CONCAT(n.name,' / ',cr.name,' ',ct.name,' ',c.name) as name from cinemas as c, networks as n, countries as cr, cities as ct where c.network_id = n.network_id and c.city_id = ct.city_id and c.country_id  = cr.country_id order by `name`")->
                    query();
             $data = Array('0'=>'-');                   
             while ($row = $r->read()) {
                $data[$row['cinema_id']] = $row['name'];
             }            
             echo CHtml::dropDownList('cinema_id',
                                      '',
                                      $data,
                                      array('id'=>'cinema_id',
                                            'ajax' => array('type'=>'POST',
                                                            'url'=>$this->createUrl('report/cinemaHalls'),
                                                            'update'=>'#soas_id'))); 
            ?>
            </td>
        </tr>
        <tr>
            <td>Зал</td>
            <td>
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
                echo CHtml::dropDownList('soas_id', '', $data, Array('id'=>'soas_id'));
                
            ?>        
        </td>
        </tr>   
        <tr>
            <td>Ролик</td>
            <td>
                <select id="promo_ident">
                <?
                    if ($models = PromoIdent::model()->findAllByAttributes(array("actived"=>1),array("order"=>"name asc"))) {
                        foreach ($models as $model) {
                            ?><option value="<?=$model->ident;?>"><?=$model->name;?></option><?
                        }
                    }
                ?>
                </select>
            </td>
        </tr>
        <tr>
            <td>Дата</td>
            <td>
            <?
                echo CHtml::textField('date', date('Y/m/d'), array("id"=>"date")).
                $this->widget('application.extensions.calendar.SCalendar', array('inputField'=>'date','language'=>'ru-UTF'),true);
            ?>
            <select id="hour">
                <? 
                    for ($i = 0; $i < 24; ++$i) {
                        ?><option value="<?=$i;?>"<? if ($i == date('H')) { ?> selected <? } ?>><?=sprintf("%02d",$i);?></option><?
                    }
                ?>
            </select>:<select id="minute">
                <? 
                    for ($i = 0; $i < 60; ++$i) {
                        ?><option value="<?=$i;?>"<? if ($i == date('i')) { ?> selected <? } ?>><?=sprintf("%02d",$i);?></option><?
                    }
                ?>
            </select>:<select id="second">
                <? 
                    for ($i = 0; $i < 60; ++$i) {
                        ?><option value="<?=$i;?>" <? if ($i == date('s')) { ?> selected <? } ?>><?=sprintf("%02d",$i);?></option><?
                    }
                ?>            
            </select>
             </td>
                </tr>
            </table>
        </td>
        </tr>
    </table>
    </form>
    
    <button onclick="add()">Создать</button>
