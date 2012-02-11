<?php
$this->pageTitle = 'Создать сеть кинотеатров';

$this->breadcrumbs=array(
	'Сети кинотеатров'=>array('index'),
	'Создать',
);

$this->menu=array(
	array('label'=>'Все сети кинотеатров', 'url'=>array('index'))
);
?>

<h1><?= $this->pageTitle ?></h1>
<?php echo $this->renderPartial('_form', array('model'=>$model)); ?>