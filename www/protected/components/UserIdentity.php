<?php

/**
 * UserIdentity represents the data needed to identity a user.
 * It contains the authentication method that checks if the provided
 * data can identity the user.
 */
class UserIdentity extends CUserIdentity
{
    private $_id;

    public function authenticate()
    {
        $record = AcUser::model()->findByAttributes(array('login' => $this->username));
        if ($record === null)
            $this->errorCode = self::ERROR_USERNAME_INVALID;
        else
            if ($record->password !== md5($this->password))
                $this->errorCode = self::ERROR_PASSWORD_INVALID;
            else
            {
                $this->_id = $record->ac_user_id;
                $this->setState('title', $record->name);
                $this->setState('is_admin', $record->is_admin);
                $this->setState('is_observer', $record->is_observer);
                $this->setState('show_log',$record->show_log);
                if ($record->is_admin)
                    $this->username = 'admin';
                $this->errorCode = self::ERROR_NONE;
            }
            return !$this->errorCode;
    }

    public function getId()
    {
        return $this->_id;
    }

    public function authenticateByCookie()
    {
         
        $user = AcUser::model()->findByAttributes(array('login' => $this->username));
        
        
        if ($user === null)
        {
            $this->errorCode = self::ERROR_UNKNOWN_IDENTITY;
        }
        else
        {
            $this->_id = $user->ac_user_id;
            $this->errorCode = self::ERROR_NONE;
            $this->afterAuth($user); // here we post-process user's data
        }
        return !$this->errorCode;
    }
}
