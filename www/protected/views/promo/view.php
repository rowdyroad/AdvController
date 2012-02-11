<?php
$this->pageTitle = $model->name;

$this->breadcrumbs=array(
	'Ролики'=>array('index'),
	$model->name,
);

$this->menu=array(
	array('label'=>'Все ролики', 'url'=>array('index')),
	array('label'=>'Загрузить', 'url'=>array('create')),
	array('label'=>'Изменить', 'url'=>array('update', 'id'=>$model->promo_id)),
	array('label'=>'Удалить', 'url'=>'#', 'linkOptions'=>array('submit'=>array('delete','id'=>$model->promo_id),'confirm'=>'Вы уверены, что хотите удалить эту запись?'))
);
?>

<h1><?= $model->name; ?></h1>

<?php $this->widget('zii.widgets.CDetailView', array(
	'data'=>$model,
	'attributes'=>array(
		'promo_id',
		'dt_added',
        'sha1',
		'comments'
	),
)); ?>

<br/><br/>
<div>
	<h3 style="display: inline">Проекторы</h3> (<?= Yii::app()->user->is_admin ? CHtml::link('добавить', $this->createUrl('lnkSoasPromo/create', array('promo_id'=>$model->promo_id))) : '' ?>)
</div>
<div>
<?php

$soasDataProvider = new CActiveDataProvider('LnkSoasPromo', array(
	'criteria'=>array(
        'condition'=>'promo_id=:promo_id',
        'params'=>array(':promo_id'=>$model->promo_id),
        'with'=>array('soas'),
    )
	//,'pagination' => array ('pageSize'=>1)
));

$gridButtons = array(
	'class'=>'CButtonColumn',
	'viewButtonUrl'=>'Yii::app()->createUrl("/soas/view", array("id" =>  $data["soas_id"]))',
	'deleteButtonUrl'=>'Yii::app()->createUrl("/lnkSoasPromo/delete", array("id" =>  $data["lnk_soas_promo_id"]))'
);

if ( !Yii::app()->user->is_admin )
	$gridButtons['template'] = '{view}';
else
	$gridButtons['template'] = '{view} {delete}';

$this->widget('zii.widgets.grid.CGridView', array(
	'id'=>'lnksoaspromo-grid',
	'dataProvider'=>$soasDataProvider,
	'columns'=>array(
		'soas.name',
		$gridButtons,
	)
));

?>
</div>
