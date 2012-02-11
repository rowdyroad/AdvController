<?

class WebUser extends CWebUser
{
    protected function restoreFromCookie()
    {
        $app = Yii::app();

        $cookie = $app->getRequest()->getCookies()->itemAt($this->getStateKeyPrefix());

        if ($cookie && !empty($cookie->value) && ($data = $app->getSecurityManager()->
            validateData($cookie->value)) !== false)
        {

            $data = unserialize($data);

            if (isset($data[0], $data[1], $data[2]))
            {

                list($id, $name, $states) = $data;

                $identity = new UserIdentity($id, '');

                $identity->authenticateByCookie();

                switch ($identity->errorCode)
                {

                    case UserIdentity::ERROR_NONE:

                        $this->changeIdentity($id, $name, $states);

                        break;

                    default:

                        # maybe I should call logout() here too

                        throw new CHttpException(401, Yii::t('yii', 'Unknown Identity'));

                        break;

                }


            }

        }

    }

}

?>