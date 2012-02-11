<?
    class FakesController extends Controller
    {        
        protected function beforeAction()
        {
           if (Yii::app()->user->is_admin)
           {
                return true;
           } 
           $this->redirect(Yii::app()->homeUrl);
           return false;
        }        
                        
        public function actionIndex()
        {
            $this->render('index');
        } 
        
        
        public function actionDelete($id) 
        {
    	    Fake::model()->deleteByPk($id);
    	    $this->redirect('fakes/index');
        }
        public function actionCreate()
        {
            $fake = new Fake;
            if (isset($_POST['Fake'])) {
                $fake->attributes = $_POST['Fake'];
                if ($fake->validate()) {
                    $fake->save();
                    $this->redirect(array('index'));
                    return;
                }
            }
            $this->render('form',array('fake'=>$fake));
        }
    };
?>