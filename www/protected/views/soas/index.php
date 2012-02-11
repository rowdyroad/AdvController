<?php
$this->pageTitle = 'Проекторы';

$this->breadcrumbs=array(
	'Проекторы'
);

$this->menu=array(
	array('label'=>'Создать', 'url'=>array('create'), 'visible' => Yii::app()->user->is_admin)
);
?>
<h1><?= $this->pageTitle ?></h1>
<?php
$gridButtons = array(
    'class'=>'CButtonColumn',
    'buttons'=>array(
            'view'=>array('url'=>'array("view","id"=>$data->soas_id)'),
            'update'=>array('url'=>'array("update","id"=>$data->soas_id)'),
            'delete'=>array('url'=>'array("delete","id"=>$data->soas_id)'),
    
        )
    );

if ( !Yii::app()->user->is_admin )
	$gridButtons['template'] = '{view}';

$dp = new CActiveDataProvider(get_class($model),array(
	'pagination' => array(
		'pageSize'=> 100
	),
    'sort'=>array('defaultOrder'=>'last_heart_beat DESC'),
));

$this->widget('zii.widgets.grid.CGridView', array(
	'id'=>'soas-grid',
	'dataProvider'=>$dp,
	'columns'=>array(
		'soas_id',
        'location',
		'cinema',        
        'hall',
          array(
            'name'=>'last_heart_beat',
            'value'=>'Utils::DateTimeFromTS(strtotime($data->last_heart_beat))'
        ),
		$gridButtons
	),
)); ?>
