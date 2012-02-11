<?php
$this->pageTitle = $model->name;

$this->breadcrumbs=array(
	'Кинотеатры'=>array('index'),
	$model->name,
);

$this->menu=array(
	array('label'=>'Все кинотеатры', 'url'=>array('index')),
	array('label'=>'Создать', 'url'=>array('create'), 'visible' => Yii::app()->user->is_admin),
	array('label'=>'Изменить', 'url'=>array('update', 'id'=>$model->cinema_id), 'visible' => Yii::app()->user->is_admin),
	array('label'=>'Удалить', 'url'=>'#', 'linkOptions'=>array('submit'=>array('delete','id'=>$model->cinema_id),'confirm'=>'Вы уверены, что хотите удалить эту запись?'), 'visible' => Yii::app()->user->is_admin),
);
?>

<h1><?= $this->pageTitle ?></h1>

<?php $this->widget('zii.widgets.CDetailView', array(
	'data'=>$model,
	'attributes'=>array(
		'name',
		'comments',
	),
)); ?>

<br/><br/>

<h3>Сервера</h3>

<div>
<?php

$promoDataProvider = new CActiveDataProvider('Soas', array(
	'criteria'=>array(
        'condition'=>'cinema_id=:cinema_id',
        'params'=>array(':cinema_id'=>$model->cinema_id)
    )
));

$gridButtons = array(
	'class'=>'CButtonColumn',
	'viewButtonUrl'=>'Yii::app()->createUrl("/soas/view", array("id" =>  $data["soas_id"]))'
);

$gridButtons['template'] = '{view}';

$this->widget('zii.widgets.grid.CGridView', array(
	'id'=>'lnksoaspromo-grid',
	'dataProvider'=>$promoDataProvider,
	'columns'=>array(
		'name',
		$gridButtons,
	)
));

?>
</div>
