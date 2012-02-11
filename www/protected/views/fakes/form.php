<div class="form">
<?
$ar = array();
if ($q = Yii::app()->db->createCommand("select s.soas_id, CONCAT(ctr.name,' ',ct.name,' / ', c.name,' Hall:',s.hall) as `name` from soas as s, cinemas as c, countries as ctr, cities as ct where s.cinema_id = c.cinema_id and c.country_id = ctr.country_id and c.city_id = ct.city_id")->query()) {
    while ($r = $q->read()) {
	$ar[$r['soas_id']] = $r['name'];
    }
}

echo new CForm(array(
    'elements'=>array(
        'soas_id'=>array(
            'type'=>'dropdownlist',
            'items'=>$ar,
            'promnt'=>'enter'
            ),            
        'fake_soas_id'=>array(
            'type'=>'dropdownlist',
            'items'=>$ar,
            'promnt'=>'enter'
        )
    ),
      'buttons' => array(
        'add' => array(
            'type' => 'submit',
            'label' => 'Добавить' ,
        ) ,
    )
),$fake);
?>
</div>