<?php
$this->pageTitle = 'Отчетность';

$this->breadcrumbs=array(
	$this->pageTitle
);
?>
<h3>Отчет по дням</h3>
<?
    
    $this->renderPartial('dayform');
?><h3>Отчет по блокам</h3><?
      $this->renderPartial('blocksform');
?>