<?php
$this->pageTitle = 'Изменить запись "'.$model->name.'"';

$this->breadcrumbs=array(
	'Пользователи'=>array('index'),
	$model->name=>array('view','id'=>$model->ac_user_id),
	'Изменить',
);

$this->menu=array(
	array('label'=>'Все пользователи', 'url'=>array('index')),
	array('label'=>'Создать', 'url'=>array('create')),
	array('label'=>'Просмотр', 'url'=>array('view', 'id'=>$model->ac_user_id)),
	array('label'=>'Новый пароль', 'url'=>array('updatePassword', 'id'=>$model->ac_user_id)),
);
?>

<h1>Изменить запись &quot;<?php echo $model->name; ?>&quot;</h1>

<?php echo $this->renderPartial('_form', array('model'=>$model, 'displayPasswordField'=>false)); ?>