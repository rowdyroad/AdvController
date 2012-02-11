<?php
$this->pageTitle = 'Изменить сеть кинотеатров "'.$model->name.'"';

$this->breadcrumbs=array(
	'Сеть кинотеатров'=>array('index'),
	$model->name=>array('view','id'=>$model->network_id),
	'Изменить',
);

$this->menu=array(
	array('label'=>'Все сети кинотеатров', 'url'=>array('index')),
	array('label'=>'Создать', 'url'=>array('create')),
	array('label'=>'Просмотр', 'url'=>array('view', 'id'=>$model->network_id))
);
?>

<h1>Изменить сервер &quot;<?php echo $model->name; ?>&quot;</h1>

<?php echo $this->renderPartial('_form', array('model'=>$model)); ?>