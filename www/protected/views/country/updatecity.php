<?php
$this->pageTitle = 'Изменение города "'.$model->name.'"';
$country  =  Country::model()->FindByPk($model->country_id);


$this->breadcrumbs=array(
	'Страны'=>array('index'),
	$country->name=>array('view','id'=>$country->country_id),
    $model->name
);

?>

<h1><?=$this->pageTitle;?></h1>
<?php echo $this->renderPartial('_cityform', array('model'=>$model)); ?>