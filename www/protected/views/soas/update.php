<?php

$view = SoasView::model()->findByAttributes(array("soas_id"=>$model->soas_id));
$name = $view->location. " ".$view->cinema." зал ".$view->hall;
$this->pageTitle = $name;

$this->breadcrumbs=array(
	'Проекторы'=>array('index'),
	 $name =>array('view','id'=>$model->soas_id),
	'Изменить',
);

$this->menu=array(
	array('label'=>'Все проекторы', 'url'=>array('index')),
	array('label'=>'Создать', 'url'=>array('create')),
	array('label'=>'Просмотр', 'url'=>array('view', 'id'=>$model->soas_id))
);
?>

<h1><?=$name;?></h1>
<?php echo $this->renderPartial('_form', array('model'=>$model)); ?>