<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="language" content="en" />

	<!-- blueprint CSS framework -->
	<link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/screen.css" media="screen, projection" />
	<link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/print.css" media="print" />
	<!--[if lt IE 8]>
	<link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/ie.css" media="screen, projection" />
	<![endif]-->

	<link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/main.css" />
	<link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/form.css" />
	<link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/custom.css" />

	<title><?= CHtml::encode($this->pageTitle).' - '.Yii::app()->name ?></title>
</head>

<body>
<div class="container" id="page">
	<div id="header">
    	    <img src="http://in-cinema.ru/images/logo_small.gif" style="padding:20px"/>
	</div><!-- header -->

	<div id="mainmenu">
		<?php
			if (Yii::app()->user->isGuest):
				$this->widget('zii.widgets.CMenu',array(
					'items'=>array(
						array('label'=>'Вход', 'url'=>array('/site/login')),
					),
				));
			else:
				$this->widget('zii.widgets.CMenu',array(
					'items'=>array(
						array('label'=>'Журнал регистраций', 'url'=>array('/event/idents'), 'visible' => Yii::app()->user->show_log),
						array('label'=>'Отчетность', 'url'=>array('/report/index')),
						array('label'=>'Ролики', 'url'=>array('/promo/idents')),
						array('label'=>'Проекторы', 'url'=>array('/soas/index')),
						array('label'=>'Кинотеатры', 'url'=>array('/network/index')),
						array('label'=>'Клиенты', 'url'=>array('/client/index')),
						array('label'=>'Пользователи', 'url'=>array('/acUser/index'), 'visible' => Yii::app()->user->is_admin),
						array('label'=>'Выход ('.Yii::app()->user->title.')', 'url'=>array('/site/logout'))
					),
				));
			endif;
		?>
	</div><!-- mainmenu -->

	<?php $this->widget('zii.widgets.CBreadcrumbs', array(
		'homeLink'=> CHtml::link('Главная', $this->createUrl('site/index')),
		'links'=>$this->breadcrumbs,
	)); ?><!-- breadcrumbs -->

	<?php echo $content; ?>

	<div id="footer">
		Copyright &copy; <?php echo date('Y'); ?>
	</div><!-- footer -->
</div><!-- page -->
</body>
</html>