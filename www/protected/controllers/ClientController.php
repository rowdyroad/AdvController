<?php

class ClientController extends Controller
{
	public $layout='//layouts/column2';
	public function actionView($id)
	{
		$this->render('view',array('model'=>$this->loadModel($id)));
	}

	public function actionCreate()
	{
		$model=new Client;
		if(isset($_POST['Client']))
		{
			$model->attributes=$_POST['Client'];
			if($model->save())
            {
				$this->redirect(array('index'));
            }
		}
		$this->render('create',array('model'=>$model));
	}

	public function actionUpdate($id)
	{
		$model=$this->loadModel($id);
		if(isset($_POST['Client']))
		{
			$model->attributes=$_POST['Client'];
			if($model->save())
				$this->redirect(array('view','id'=>$model->client_id));
		}

		$this->render('update',array(
			'model'=>$model,
		));
	}

	/**
	 * Deletes a particular model.
	 * If deletion is successful, the browser will be redirected to the 'index' page.
	 * @param integer $id the ID of the model to be deleted
	 */
	public function actionDelete($id)
	{
		if(Yii::app()->request->isPostRequest)
		{
			// we only allow deletion via POST request
			$this->loadModel($id)->delete();

			// if AJAX request (triggered by deletion via index grid view), we should not redirect the browser
			if(!isset($_GET['ajax']))
				$this->redirect(isset($_POST['returnUrl']) ? $_POST['returnUrl'] : array('index'));
		}
		else
			throw new CHttpException(400,'Invalid request. Please do not repeat this request again.');
	}

	/**
	 * Lists all models.
	 */
	public function actionIndex()
	{
		$model=new Client;
		$model->unsetAttributes();  // clear any default values
		if(isset($_GET['Client']))
			$model->attributes=$_GET['Client'];

		if (!Yii::app()->user->is_admin)
			$this->layout = '//layouts/column1';

		$this->render('index',array('model'=>$model));
	}

	/**
	 * Returns the data model based on the primary key given in the GET variable.
	 * If the data model is not found, an HTTP exception will be raised.
	 * @param integer the ID of the model to be loaded
	 */
	public function loadModel($id)
	{
		$model=Client::model()->findByPk((int)$id);
		if($model===null)
			throw new CHttpException(404,'The requested page does not exist.');
		return $model;
	}
        
    protected function performAjaxValidation($model)
	{
		if(isset($_POST['ajax']) && $_POST['ajax']==='Client-form')
		{
			echo CActiveForm::validate($model);
			Yii::app()->end();
		}
	}
}
