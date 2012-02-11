<?
$this->pageTitle = 'Отчет по блокам';

$this->breadcrumbs=array(
    'Отчетность'=>$this->createUrl('report/index'),
	$this->pageTitle
);

$this->renderPartial('blocksform');

$blocks = Array();

$last = 0;
while ($r = $data->read()) {  
  if ($r['timestamp'] - $last > 600) {
    $blocks[] = Array();    
    $last = $r['timestamp'];
  }
  $z = &$blocks[count($blocks)-1];  
  $z[] = $r;
}

?><h2>Отчет из <?=$info['network_name'];?> <?=$info['cinema_name'];?> зал <?=$info['hall'];?> за <?=Utils::DateFromTS($info['timestamp']); ?></h2><?

foreach ($blocks as $block) {
    if (empty($block)) continue;
    ?><table style="width:600px; border:1px solid black">
        <tr style="background:silver;"><td colspan="2">Блок за <?=date('H:i:s',$block[0]['timestamp']);?> - <?=date('H:i:s',$block[count($block)-1]['timestamp']);?><td></tr>
        <?
            $i = 1;
            foreach ($block as $item) {
                ?><tr><td><?=$i++;?>.  <?=$item['promo_name'];?></td><td><?=date('H:i:s',strtotime($item['dt_registered']));?><td></tr><?        
            }
        ?>
    </table> <?       
}
?>