<?php

$this->pageTitle = $model->name;;

$this->breadcrumbs=array(
	'Пользователи'=>array('index'),
	$model->name,
);

$this->menu=array(
	array('label'=>'Все пользователи', 'url'=>array('index')),
	array('label'=>'Создать', 'url'=>array('create')),
	array('label'=>'Изменить', 'url'=>array('update', 'id'=>$model->ac_user_id)),
	array('label'=>'Новый пароль', 'url'=>array('updatePassword', 'id'=>$model->ac_user_id)),
	array('label'=>'Удалить', 'url'=>'#', 'linkOptions'=>array('submit'=>array('delete','id'=>$model->ac_user_id),'confirm'=>'Вы уверены, что хотите удалить эту запись?'))
);
?>

<h1><?= $this->pageTitle ?></h1>

<?php $this->widget('zii.widgets.CDetailView', array(
	'data'=>$model,
	'attributes'=>array(
		'login',
		'is_admin',
		'is_observer',
	)
)); ?>
