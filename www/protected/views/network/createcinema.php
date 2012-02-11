<?php
$network  =  Network::model()->FindByPk($model->network_id);
$this->pageTitle = 'Добавление кинотеатра в сеть "'.$network->name.'"';

$this->breadcrumbs=array(
	'Сети кинотеатров'=>array('index'),
    $network->name=>array('view','id'=>$network->network_id)
);

?>

<h1><?=$this->pageTitle;?></h1>
<?php echo $this->renderPartial('_cinemaform', array('model'=>$model)); ?>