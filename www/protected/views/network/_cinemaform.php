<div class="form">

<?php $form=$this->beginWidget('CActiveForm', array(
	'id'=>'cinema-form',
	'enableAjaxValidation'=>false,
)); ?>

	<p class="note">Поля, отмеченные <span class="required">*</span>, обязательны для заполнения.</p>

	<?php echo $form->errorSummary($model); ?>

    <? echo $form->hiddenField($model,'network_id');?>
	<div class="row">
		<?php echo $form->labelEx($model,'name'); ?>
		<?php echo $form->textField($model,'name',array('size'=>60,'maxlength'=>100)); ?>
		<?php echo $form->error($model,'name'); ?>
	</div>
    <? $data = array_merge(array("0"=>"---"), CHtml::listData(Country::model()->findAll(),'country_id','name'));?>
	<div class="row"> 
		<?php echo $form->labelEx($model,'country'); ?>
		<?php echo $form->dropDownList($model,'country_id',$data,array(
            'ajax' => array('type'=>'POST','url'=>CController::createUrl('network/getCities'),'update'=>'#Cinema_city_id'))); ?>
		<?php echo $form->error($model,'country_id'); ?>
	</div>
	<div class="row">
		<?php echo $form->labelEx($model,'city'); ?>
        <?
            $ar = array('0'=>'-');
             
            if ($model->country_id) {
                
                $country = $model->country_id;
                $r = Yii::app()->db->createCommand('select city_id, name from cities where country_id = :country_id')->
                bindParam(':country_id',$country)->
                query();
                
                while ($row = $r->read())
                {
                    $ar[$row['city_id']] = $row['name'];
                }                 
            }
        ?>
		<?php echo $form->dropDownList($model,'city_id',$ar); ?>
		<?php echo $form->error($model,'city_id'); ?>
	</div>
	<div class="row">
		<?php echo $form->labelEx($model,'halls'); ?>
		<?php echo $form->textField($model,'halls',array('size'=>60,'maxlength'=>100)); ?>
 	  <?php echo $form->error($model,'halls'); ?>
	</div>
	<div class="row">
		<?php echo $form->labelEx($model,'annotation'); ?>
		<?php echo $form->textArea($model,'annotation',array('rows'=>6, 'cols'=>50)); ?>
		<?php echo $form->error($model,'annotation'); ?>
	</div>

	<div class="row buttons">
		<?php echo CHtml::submitButton($model->isNewRecord ? 'Создать' : 'Сохранить'); ?>
	</div>

<?php $this->endWidget(); ?>

</div><!-- form -->