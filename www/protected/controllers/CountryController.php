<?php

class CountryController extends Controller
{
	/**
	 * @var string the default layout for the views. Defaults to '//layouts/column2', meaning
	 * using two-column layout. See 'protected/views/layouts/column2.php'.
	 */
	public $layout='//layouts/column2';
	/**
	 * Displays a particular model.
	 * @param integer $id the ID of the model to be displayed
	 */
     
     public function accessRules()
     {
        $data = parent::accessRules();
        
        $data[1]['actions'][] = 'deleteCity';
        $data[1]['actions'][] = 'updateCity';
        return $data;
        
     }
     
     
	public function actionView($id)
	{
        $city = new City;
        $city->country_id = $id;
        if (isset($_POST['City']))
        {
            $city->attributes=$_POST['City'];
            $city->country_id = $id;
            $city->save();
        }
		$this->render('view',array('model'=>$this->loadModel($id),'city'=>$city));
	}

	/**
	 * Creates a new model.
	 * If creation is successful, the browser will be redirected to the 'view' page.
	 */
	public function actionCreate()
	{
		$model=new Country;
		if(isset($_POST['Country']))
		{
			$model->attributes=$_POST['Country'];
			if($model->save())
            {
				$this->redirect(array('index'));
            }
		}
		$this->render('create',array('model'=>$model));
	}

	/**
	 * Updates a particular model.
	 * If update is successful, the browser will be redirected to the 'view' page.
	 * @param integer $id the ID of the model to be updated
	 */
	public function actionUpdate($id)
	{
		$model=$this->loadModel($id);

		// Uncomment the following line if AJAX validation is needed
		// $this->performAjaxValidation($model);

		if(isset($_POST['Country']))
		{
			$model->attributes=$_POST['Country'];
			if($model->save())
				$this->redirect(array('view','id'=>$model->country_id));
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
		$model=new Country;
		$model->unsetAttributes();  // clear any default values
		if(isset($_GET['Country']))
			$model->attributes=$_GET['Country'];

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
		$model=Country::model()->findByPk((int)$id);
		if($model===null)
			throw new CHttpException(404,'The requested page does not exist.');
		return $model;
	}
    
    public function actionDeleteCity($id)
    {
        $model = City::model()->findByPk((int)$id);
        if ($model)
        {
            $model->delete();
        }
    }
    
    public function actionUpdateCity($id)
    {
        $model = City::model()->findByPk($id);
        if (!$model)
        {
            $this->redirect(array('site/index'));
            return; 
        }
        
        if (isset($_POST['City']))
        {
            $model->attributes = $_POST['City'];
            if ($model->save())
            {
				$this->redirect(array('view','id'=>$model->country_id));
            }
        }
        else
        {                        
            $this->render("updatecity",array("model"=>$model));               
        }        
    }

	/**
	 * Performs the AJAX validation.
	 * @param CModel the model to be validated
	 */
	protected function performAjaxValidation($model)
	{
		if(isset($_POST['ajax']) && $_POST['ajax']==='Country-form')
		{
			echo CActiveForm::validate($model);
			Yii::app()->end();
		}
	}
}
