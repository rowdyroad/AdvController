<?php
$this->pageTitle = 'Создать кинотеатр';

$this->breadcrumbs=array(
	'Кинотеатр'=>array('index'),
	'Создать',
);

$this->menu=array(
	array('label'=>'Все кинотеатры', 'url'=>array('index'))
);
?>

<h1><?= $this->pageTitle ?></h1>

<?php echo $this->renderPartial('_form', array('model'=>$model)); ?>