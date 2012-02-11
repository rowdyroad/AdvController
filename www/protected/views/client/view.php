<?php

$this->pageTitle = $model->name;;

$this->breadcrumbs=array(
	'Клиенты'=>array('index'),
	$model->name,
);

$this->menu=array(
	array('label'=>'Добавить', 'url'=>array('create')),
	array('label'=>'Изменить', 'url'=>array('update', 'id'=>$model->client_id)),
	array('label'=>'Удалить', 'url'=>'#', 'linkOptions'=>array('submit'=>array('delete','id'=>$model->client_id),'confirm'=>'Вы уверены, что хотите удалить эту запись?'))
);
?>

<h1><?= $this->pageTitle ?></h1>

<?php $this->widget('zii.widgets.CDetailView', array(
	'data'=>$model,
	'attributes'=>array('name','about')
)); ?>
