<?php
$this->pageTitle = 'Ролики';

$this->breadcrumbs=array($this->pageTitle);

$this->menu=array(
	array('label'=>'Загрузить', 'url'=>array('create')),
);
?>
<h1><?= $this->pageTitle ?></h1>
<?
$gridButtons = array('class'=>'CButtonColumn');
if ( !Yii::app()->user->is_admin )
	$gridButtons['template'] = '{view}';
    
    
$data = $model->search();
$data->pagination->pageSize = 100;
$this->widget('zii.widgets.grid.CGridView', array(
	'id'=>'promo-grid',
	'dataProvider'=>$data,
	'columns'=>array(
		'dt_added',
		'name',
 	      array(
            'header'=>'Клиент',
			'name'=>'client.name',
		),
		$gridButtons
	),
)); ?>
