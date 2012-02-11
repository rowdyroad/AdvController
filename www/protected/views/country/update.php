<?php
$this->pageTitle = 'Редактирование страны "'.$model->name.'"';

$this->breadcrumbs=array(
	'Страны'=>array('index'),
	$model->name=>array('view','id'=>$model->country_id),
	'Изменить',
);

$this->menu=array(
	array('label'=>'Все страны', 'url'=>array('index')),
	array('label'=>'Добавить', 'url'=>array('create')),
	array('label'=>'Просмотр', 'url'=>array('view', 'id'=>$model->country_id))
);
?>

<h1><?=$this->pageTitle;?></h1>
<?php echo $this->renderPartial('_form', array('model'=>$model)); ?>