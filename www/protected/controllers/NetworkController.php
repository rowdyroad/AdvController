<?php

class NetworkController extends Controller
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
	public function actionView($id)
	{
		$this->render('view',array(
			'model'=>$this->loadModel($id),
		));
	}
   public function accessRules()
     {
        $data = parent::accessRules();
        
        $data[1]['actions'][] = 'createCinema';
        $data[1]['actions'][] = 'deleteCinema';        
        $data[1]['actions'][] = 'updateCinema';      
        $data[1]['actions'][] = 'getCities';                      
        $data[0]['actions'][] = 'viewCinema';     
        return $data;
        
     }

	/**
	 * Creates a new model.
	 * If creation is successful, the browser will be redirected to the 'view' page.
	 */
	public function actionCreate()
	{
		$model=new Network;
		if(isset($_POST['Network']))
		{
			$model->attributes=$_POST['Network'];
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

		if(isset($_POST['Network']))
		{
			$model->attributes=$_POST['Network'];
			if($model->save())
				$this->redirect(array('view','id'=>$model->network_id));
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
		$model=new Network;
		$model->unsetAttributes();  // clear any default values
		if(isset($_GET['Network']))
			$model->attributes=$_GET['Network'];

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
		$model=Network::model()->findByPk((int)$id);
		if($model===null)
			throw new CHttpException(404,'The requested page does not exist.');
		return $model;
	}
    
    
    public function actionCreateCinema($id)
    {
        $cinema = new Cinema;
    
        if (isset($_POST['Cinema']))
        {
            $cinema->attributes = $_POST['Cinema'];
            if ($cinema->save())
            {
		$this->redirect(array('view','id'=>$cinema->network_id));
            }
        }
        else
        {
            $cinema->network_id = $id;
            $this->render('createcinema',array("model"=>$cinema));
        }                                    
    }
    
    public function actionGetCities()
    {
        $id = (int)$_POST['Cinema']['country_id'];
        
        $data=City::model()->findAll("country_id=$id");     
        $data=CHtml::listData($data,'city_id','name');
        foreach($data as $value=>$name)
        {
            echo CHtml::tag('option',array('value'=>$value),CHtml::encode($name),true);
        }
    }
    
    public function actionDeleteCinema($id)
    {
		if(Yii::app()->request->isPostRequest)
		{
			// we only allow deletion via POST request
			Cinema::model()->findByPk($id)->delete();
			// if AJAX request (triggered by deletion via index grid view), we should not redirect the browser
			if(!isset($_GET['ajax']))
				$this->redirect(isset($_POST['returnUrl']) ? $_POST['returnUrl'] : array('index'));
		}
		else
			throw new CHttpException(400,'Invalid request. Please do not repeat this request again.');

    }
    
    public function actionUpdateCinema($id)
    {
        $cinema = Cinema::model()->findByPk($id);
        if (isset($_POST['Cinema'])) {
            $cinema->attributes = $_POST['Cinema'];
            if ($cinema->validate()) {
                $cinema->save();
            }
        }
        $this->render('updatecinema',array('model'=>$cinema));
    }
    
    public function actionViewCinema($id)
    {
        $cinema = Cinema::model()->findByPk($id);
        $this->render('viewcinema',array('model'=>$cinema));
    
    }

	/**
	 * Performs the AJAX validation.
	 * @param CModel the model to be validated
	 */
	protected function performAjaxValidation($model)
	{
		if(isset($_POST['ajax']) && $_POST['ajax']==='Network-form')
		{
			echo CActiveForm::validate($model);
			Yii::app()->end();
		}
	}
}
