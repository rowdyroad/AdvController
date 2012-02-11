<?php
$this->pageTitle = 'Отчены по дням';

$this->breadcrumbs=array(
    'Отчетность'=>$this->createUrl('report/index'),
	$this->pageTitle
);

$this->renderPartial('dayform');

$days = Array();
for ($date = $date_begin; $date < $date_end; $date+=24*3600) {
    $days[] = $date;    
}

$z = Array();
while ($r = $data->read()) {
    $z[$r['hall']][$r['promo_name']][$r['timestamp']]  = $r['count'];
}

?>
<h2>Отчет из <?=$info['network_name'];?> <?=$info['cinema_name'];?> за период с <?=Utils::DateFromTS($days[0]); ?> по <?=Utils::DateFromTS($days[count($days)-1]);?></h2>

<div style="width: 880px;overflow:auto;">
<table style="background: white;" ><?
foreach ($z as $hall=>$hall_data) {    
    ?><tr>
        <td>Зал №<?=$hall?></td>
        <td><table style="border: 1px solid black;">
        <tr style="font-weight: bold;background:silver"><td>Ролик \ Дата</td>        
        <?
            foreach ($days as $day) { 
                ?><td><?=Utils::ShortDateFromTs($day);?></td><?
            }
            ?><td>Итого</td></tr><?
            $totals = Array();
            $g_total = 0;
            foreach ($hall_data as $promo=>$promo_days) {
                ?><tr><td><?=$promo;?></td><?              
                    $total = 0;  
                    foreach ($days as $day) {
                        $d_c = isset($promo_days[$day]) ? $promo_days[$day] : 0;
                        
                        if (! Yii::app()->user->is_admin && $d_c) {
                    	    if ($d_c == 1) {
                    		$d_c = 0;
                    	    } else if ($d_c < 3) { 
                    		$d_c += 3;
                    	    } else if ($d_c >= 3 && $d_c < 5) {
                    		$d_c += 2;
                    	    }
                        } 
                        if (!isset($totals[$day])) $totals[$day] = 0;
                        $totals[$day] += $d_c;
                        $total+=$d_c;
                        ?><td><?=$d_c;?></td><?
                    }
                    ?><td style="background:#efefef"><?=$total;?></td><?
                    $g_total+=$total;
            }
        ?>
        <tr style="background:#efefef;"><td>Итого</td>
        <?
        foreach ($days as $day) { 
          ?><td><?=$totals[$day];?></td><?
        }
        ?>
        
        <td><?=$g_total;?></td></tr>
        </table></td>    
    </tr><?    
}
?></table>
</div><?