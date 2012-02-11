<?php
$this->pageTitle = 'Добавление страны';

$this->breadcrumbs=array(
	'Страны'=>array('index'),
	'Добавление',
);

$this->menu=array(
	array('label'=>'Все страны', 'url'=>array('index'))
);
?>

<h1><?= $this->pageTitle ?></h1>
<?php echo $this->renderPartial('_form', array('model'=>$model)); ?>