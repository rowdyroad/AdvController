<?php
$this->pageTitle = 'Изменить пароль "'.$model->name.'"';

$this->breadcrumbs=array(
	'Пользователи'=>array('index'),
	$model->name=>array('view','id'=>$model->ac_user_id),
	'Изменить пароль',
);

$this->menu=array(
	array('label'=>'Все пользователи', 'url'=>array('index')),
	array('label'=>'Создать', 'url'=>array('create')),
	array('label'=>'Просмотр', 'url'=>array('view', 'id'=>$model->ac_user_id))
);

?>

<h1>Изменить пароль &quot;<?php echo $model->name; ?>&quot;</h1>

<?php echo $this->renderPartial('_formPassword', array('model'=>$model)); ?>