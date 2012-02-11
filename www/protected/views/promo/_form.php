<div class="form">

<?php $form=$this->beginWidget('CActiveForm', array(
	'id'=>'promo-form',
	'enableAjaxValidation'=>false,
	'htmlOptions' => array('enctype'=>'multipart/form-data')
)); ?>

	<p class="note">Поля, отмеченные <span class="required">*</span>, обязательны для заполнения.</p>

	<?php echo $form->errorSummary($model); ?>

	<div class="row">
		<?php echo $form->labelEx($model,'name'); ?>
		<?php echo $form->textField($model,'name',array('size'=>60,'maxlength'=>255)); ?>
		<?php echo $form->error($model,'name'); ?>
	</div>
	
	<div class="row">
		<?php echo $form->labelEx($model,'file'); ?>
		<?php echo $form->fileField($model,'file', array('size'=>50)); ?>
		<?php echo $form->error($model,'file'); ?>
		<?php if ($action == 'update') echo '<div class="hint">Оставьте это поле пустым, если не хотите заменять текущий файл</div>'; ?>
	</div>	
    <div class="row">
        <?php echo $form->labelEx($model,'client'); ?>
        <?php echo $form->dropDownList(
						$model,'client_id',
						array( '' => '- выберите клиента -' ) + CHtml::listData(Client::model()->findAll(), 'client_id', 'name')

		); ?>
		<?php echo $form->error($model,'cinema_id'); ?>
    </div>

	<div class="row">
		<?php echo $form->labelEx($model,'comments'); ?>
		<?php echo $form->textArea($model,'comments',array('rows'=>6, 'cols'=>50)); ?>
		<?php echo $form->error($model,'comments'); ?>
	</div>

	<div class="row buttons">
		<?php echo CHtml::submitButton($model->isNewRecord ? 'Загрузить' : 'Сохранить'); ?>
	</div>

<?php $this->endWidget(); ?>

</div><!-- form -->