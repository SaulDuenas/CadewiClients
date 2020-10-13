<?php
use Restserver\Libraries\REST_Controller;
defined('BASEPATH') OR exit('No direct script access allowed');

// This can be removed if you use __autoload() in config.php OR use Modular Extensions
/** @noinspection PhpIncludeInspection */
//To Solve File REST_Controller not found
require APPPATH . 'libraries/REST_Controller.php';
require APPPATH . 'libraries/Format.php';

class Usuarios extends REST_Controller {

    public function __construct() {
        parent::__construct();
        $this->load->model('users_model');
    }

    public function index_get() {
       // echo 'todos los usuarios';
      try {

          $users = $this->users_model->get();

          if(!is_null($users)){
              $this->response(array('Estado' => Status_OK,
                                    'Excepcion' => '',
                                    'Usuarios' => $users),200);
          } else{
              $this->response(array( 'Estado' => Status_FAIL,
                                     'Excepcion' => 'No hay usuarios registrados',
                                     'Usuarios' => null),200);
          }

      } catch (Exception $e) {
          
          $this->response(array('Estado' => Status_FAIL,
                                'Excepcion' => $e->getMessage(),
                                'Usuarios' => null),200);
      }

    }

    public function find_get() {
       // echo 'Usuario Id='.$user;
      try {
        $Receive =  $this->input->get(NULL, TRUE); // returns all POST items with XSS filter
        $search='';

         if(isset($Receive['Search'])) {
            $search = strtoupper(trim($Receive['Search']));     
         }
         else{
            $search = '';
         }

         $user = $this->users_model->find($search,array(STATS_NEW,STATS_MOFIDY));

         $this->response(array('Estado' => Status_OK,
                               'Excepcion' => '',
                               'Usuarios' => $user),200);

       } catch (Exception $e) {
          
          $this->response(array('Estado' => Status_FAIL,
                                'Excepcion' => $e->getMessage(),
                                'Usuarios' => null),200);
       }
     
    }


    public function login_post() {
         
      try {
         $DataPost = $this->input->post(NULL, TRUE); // returns all POST items with XSS filter

         if(!$DataPost) {
              $this->response(null,400);
         }

         $email = trim($DataPost['Email']);
         $password = trim($DataPost['Passw']);

         // find user
         $user = $this->users_model->get($email,array(STATS_NEW,STATS_MOFIDY));

         $status = 0;
         $message='';
         $users=null;

         // User Exist ?
         if($user != false) {
    	
    		   if( $user->Password === $password) {

    	          $status = Status_OK;
                $message='clave correcta';
                $users=array($user);
           }

           elseif ( $user->Password != $password ) {

                $status = Status_FAIL;
                $message='clave incorrecta';
                $users=null;
  	      }

        }

        else {

                $status = Status_FAIL;
                $message='Usuario no encontrado';
                $users=null;
        }


        $this->response(array( 'Estado' => $status,
                               'Excepcion' => $message,
                               'Usuarios' => $users),200);


      } catch (Exception $e) {
          
          $this->response(array('Estado' => Status_FAIL,
                                'Excepcion' => $e->getMessage(),
                                'Usuarios' => null),200);
      }


    }


    public function index_post() {
     
      try {
          $userDataPost =  $this->input->post(NULL, TRUE); // returns all POST items with XSS filter

          if(!$userDataPost){
              $this->response(null,400);
          }

          $email = $this->input->post('Email');
          $user = $this->users_model->get($email);
        
          if(!$user) {
             
              $status = 0;
              $message='';
              $users=null;

              $new_user = $this->users_model->save($userDataPost);

              if(!is_null($new_user)) {

                  $status = Status_OK;
                  $message='Usuario agregado satisfactoriamente';
                  $users=array($new_user);

              } else {

                  $status = Status_FAIL;
                  $message='Algo ha fallado en el servidor';
                  $users=null;
              }
          }
          else{
              // SADB: user exits

              if ($user['Estatus'] === STATS_DELETE) {

                $user_update = $this->users_model->update($userDataPost);

                if(!is_null($user_update)) {

                  $status = Status_OK;
                  $message='Actualización generada satisfactoriamente';
                  $users=$user_update;

                } else {

                  $status = Status_FAIL;
                  $message='Algo ha fallado en el servidor';
                  $users=null;
                } 

              }
              elseif ($user['Estatus'] === STATS_MOFIDY || $user['Estatus'] === STATS_NEW) {
                $status = Status_FAIL;
                $message='Este correo ya esta siendo utilizado, utilize otro diferente';
                $users=null;
              }

          }

          $this->response(array('Estado' => $status,
                                'Excepcion' => $message,
                                'Usuarios' => $users),200);

       } catch (Exception $e) {
          
          $this->response(array('Estado' => Status_FAIL,
                                'Excepcion' => $e->getMessage(),
                                'Usuarios' => $users),200);
       }

    }

    public function update_post() {
    
      try {
             $userDataPost = $this->input->post(NULL, TRUE); // returns all POST items with XSS filter

             if(!$userDataPost) {
                  $this->response(null,400);
             }

             $email = trim($userDataPost['Email']);
            // SADB: find user
             $user = $this->users_model->get($email);
        
             $status = 0;
             $message='';
             $users=null;

             // User Exist ?
             if($user != false) {

                 $user_update = $this->users_model->update($userDataPost);

                 if(!is_null($user_update)) {

                    $status = Status_OK;
                    $message='Actualización generada satisfactoriamente';
                    $users=array($user_update);

                 } else {

                  $status = Status_FAIL;
                  $message='Algo ha fallado en el servidor';
                  $users=null;
                }

             }
             else {
                $status = Status_FAIL;
                $message='Usuario no encontrado';
                $users=null; 
             }

             $this->response(array( 'Estado' => $status,
                                    'Excepcion' => $message,
                                    'Usuarios' => $users),200);


       } catch (Exception $e) {
                
            $this->response(array('Estado' => Status_FAIL,
                                  'Excepcion' => $e->getMessage(),
                                  'Usuarios' => null),200);
      }

    }

    public function index_delete($userId) {
        
      try {
          if(!is_null($userId)){
              $this->response(array('Estado' => Status_OK,
                                    'Excepcion' => '',  
                                    'Usuarios' => null),200);
          }
          else{
              $this->response(array('Estado' => Status_FAIL,
                                     'Excepcion' => 'El usuario no existe',
                                     'Usuarios' => null),400);
          }

        } catch (Exception $e) {
          
          $this->response(array('Estado' => Status_FAIL,
                                'Excepcion' => $e->getMessage(),
                                'Usuarios' => null),200);
       }

    }

}