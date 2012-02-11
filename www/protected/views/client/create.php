<?php
$this->pageTitle = 'Добавление нового клиента';

$this->breadcrumbs=array('Клиенты'=>array('index'),'Добавление');

$this->menu=array(array('label'=>'Все клиенты', 'url'=>array('index')));
?>

<h1><?= $this->pageTitle ?></h1>
<?php echo $this->renderPartial('_form', array('model'=>$model)); ?>