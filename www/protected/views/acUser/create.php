<?php
$this->pageTitle = 'Создать пользователя';

$this->breadcrumbs=array(
	'Пользователи'=>array('index'),
	'Создать',
);

$this->menu=array(
	array('label'=>'Все пользователи', 'url'=>array('index')),
);
?>

<h1><?= $this->pageTitle ?></h1>

<?php echo $this->renderPartial('_form', array(
		'model'=>$model, 
		'displayPasswordField'=>true
	)); ?>