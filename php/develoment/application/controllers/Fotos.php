<?php
use Restserver\Libraries\REST_Controller;
defined('BASEPATH') OR exit('No direct script access allowed');

//define("URL", 'http://localhost/pictures/');
define("URL", 'http://www.cadewigroup.com/pictures/');

// This can be removed if you use __autoload() in config.php OR use Modular Extensions
/** @noinspection PhpIncludeInspection */
//To Solve File REST_Controller not found
require APPPATH . 'libraries/REST_Controller.php';
require APPPATH . 'libraries/Format.php';

class Fotos extends REST_Controller {


	public function __construct(){
        parent::__construct();
        $this->load->model('Photo_model');
        $this->load->model('Parameter_model');
    }


	public function header_get() {
	
		try {

			$Receive =  $this->input->get(NULL, TRUE); // returns all POST items with XSS filter

			if( isset($Receive['Address'])) {
				$address  = $Receive['Address'];
			}
			else{
				$address  = null;
			}

			//$this->response($Receive);

			if(is_null($address)) {
				$this->response(null,400);
			}

			$status = 0;
	        $message='';
	        $photo=null;

	        $url = $this->Parameter_model->getStringValue('url','sistema');

			$PhotoHead = $this->Photo_model->get_head($address,0);

			if($PhotoHead != false) {

				$photos = array(); // creo el array

				foreach ($PhotoHead as $row) {

					//$clients[] =  $this->get_client($row['Cliente']);

					$photos[] = array('Imagen' => $row['Imagen'],
				                      'Direccion' => $row['Direccion'],
				                      'Cliente' => $row['Cliente'],
				                      'Bynary' => $row['Bynary'],
				                      'Nombre' => $row['Nombre'],
				                      'Ruta' => $url.$row['Ruta'],
				                      'Nota'=> $row['Nota'],
				                      'FechaHora'=> $row['FechaHora'],
				                      'Estatus' => $row['Estatus']
				                     );

				}	

				$status = Status_OK;
	        	$message='';
	       	 	$photo=$photos;

			} else {

				$status = Status_FAIL;
	        	$message='No se encontraron  fotos asociadas para la dirección';
	       	 	$photo=null;

			}

			$this->response(array('Estado' => $status,
								  'Excepcion' => $message,
				                  'Fotos' => $photo),200);


		} catch (Exception $e) {
    		
 			$this->response(array('Estado' => Status_FAIL,
		                          'Excepcion' => $e->getMessage(),
		                          'Fotos' => null),200);
		}
	}

	public function binary_get() {
	
		try {

			$Receive =  $this->input->get(NULL, TRUE); // returns all POST items with XSS filter
		
			//$this->response($Receive);

			if(is_null($Receive)) {
				$this->response(null,400);
			}

			$ParamPhotoId =  $Receive['PhotoId'];

			$PhotoResult = $this->Photo_model->get_bynary($ParamPhotoId);

			if($PhotoResult != false){
				$this->response(array('Estado' => Status_OK,
				                      'Excepcion' => '',	
				                      'Fotos' => $PhotoResult),200);
			} else {
				$this->response(array('Estado' => Status_FAIL,
									  'Excepcion' => 'Id de foto no identificada',
				                      'Fotos' => null),200);
			}

		} catch (Exception $e) {
    		
 			$this->response(array('Estado' => Status_FAIL,
		                          'Excepcion' => $e->getMessage(),
		                          'Fotos' => null),200);
		}
	}


	public function save_photo_post(){
     
     	try {
		        $DataPost = $this->input->post(NULL, TRUE); // returns all POST items with XSS filter
				
		//		$this->response($this->_setImage($DataPost),400);

		        if(!$DataPost){
		            $this->response(null,400);
		        }

				$upload_path = $this->Parameter_model->getStringValue('path_imagenes','sistema');
		        /* SADB: 01 - Foto en file system  */
		       	$path="../".$upload_path.$DataPost['Name']; 						 // SADB: Path file
		     	file_put_contents($path, base64_decode($DataPost['Binary']));   // SADB: Save file local system
		        $DataPost['Path'] = URL.$DataPost['Name'];   // *** SADB: Cambiar segun elección                       
		        $DataPost['Binary'] = NULL;
		        
		        /* SADB: END  */
				
				/* SADB: 02 - Foto en base de datos */
		 		
		    //    $bytesFiles=file_get_contents($path);		// SADB: Get Bytes of file 
		    //    $DataPost['Binary'] = $bytesFiles;          
		 		
		 		/* SADB: END  */
		       

		      	$parameters = $this->_setImage($DataPost);

		        $query = $this->Photo_model->save($parameters);

		        if($query != false) {
		            $this->response(array('Estado' => Status_OK,
		                                  'Excepcion' => 'Imagen almacenada satisfactoriamente',
		                                  'Fotos' => $query),200);
		        } else {
		            $this->response(array('Estado' => Status_FAIL,
		                                  'Excepcion' => 'Fallo al guardar referencia en base de datos',
		                                  'Fotos' => null),200);
		        }

        } catch (Exception $e) {
    		
 			$this->response(array('Estado' => Status_FAIL,
		                          'Excepcion' => $e->getMessage(),
		                          'Fotos' => null),200);
		}
        
    }


    public function update_photo_put() {
      
	    try {

	        $Receive =  $this->input->get(NULL, TRUE); // returns all POST items with XSS filter
			
	        if(!$Receive){
	            $this->response(null,400);
	        }

	        $photoId = $Receive['PhotoId'];

	        $parameters = $this->_setImage($Receive);

	      //  $this->response(array($photoId, $parameters),400);

	        $query = $this->Photo_model->update($photoId, $parameters);

	        if($query != false){
	            $this->response(array('Estado' => Status_OK,
	                                  'Excepcion' => 'Imagen actualizada satisfactoriamente',
	                                  'Fotos' => $query),200);
	        } else {
	            $this->response(array('Estado' => Status_FAIL,
	                                  'Excepcion' => 'Algo ha fallado en el servidor',
	                                  'Fotos' => null),200);
	        }

        } catch (Exception $e) {
    		
 			$this->response(array('Estado' => Status_FAIL,
		                          'Excepcion' => $e->getMessage(),
		                          'Fotos' => null),200);
		}
        
    }

    public function upload_post() { 

		try {

	    	$DataPost = $this->input->post(NULL, TRUE); // returns all POST items with XSS filter	

	    	$upload_path = $this->Parameter_model->getStringValue('path_imagenes','sistema');

	    	//$this->response(array('Parameter' => $upload_path ),400);

	    	$status = 0;
            $message='';
            $foto=null;

	        if ( !empty($_FILES) ) {

	            $fileName = $_FILES['file']['name'];
	            $config['upload_path'] = "../".$upload_path;
	            $config['file_name'] = $fileName;
	            $config['allowed_types'] = '*';
	            $config['encrypt_name'] = FALSE;
	            $config['file_ext_tolower']=FALSE;
	            $config['overwrite']=TRUE;
	            $this->load->library('upload', $config); 

	            if (!$this->upload->do_upload("file")) {

	            	$status = Status_FAIL;
            		$message='Error: '.$this->upload->display_errors();
            		$foto=null;

	            } else {

	           // 	$this->response(array('Estado' => Status_FAIL,
		   //                               'Excepcion' => $this->upload->data(),
		   //                               'Fotos' => null),200);

	            	$data = $this->upload->data();
	            	$fileName = $data['orig_name'];
	            	$DataPost['Path'] =  $upload_path.$fileName;
	            	$DataPost['Name'] = $fileName;
	            	$Data = $this->_setImage($DataPost);

	            	$query = $this->Photo_model->save($Data);

			        if($query != false) {

						$status = Status_OK;
            			$message='Imagen almacenada satisfactoriamente';
            			$foto=$query;

			        } else {

			        	$status = Status_FAIL;
            			$message='Fallo al guardar referencia en base de datos';
            			$foto=null;
			        }               
	                
	              //  $this->response(array('Message' => 'opcion 02'),200); 
/*
	                $raw_name = $this->upload->data('raw_name');
	                $inputFileName =  $this->upload->data('file_name');
	                $file_ext = $this->upload->data('file_ext');
	                $config['image_library'] = 'gd2';
	                $config['source_image'] = $this->upload->data('full_path');
	                $config['create_thumb'] = TRUE;
	                $config['maintain_ratio'] = TRUE;
	                $config['width']         = 48;
	                $config['height']       = 48;
	                $this->load->library('image_lib', $config);
	                $this->image_lib->resize();
	                $status['status'] = 1;   

	      */         	

	            }
	        } else {

	        	$status = Status_FAIL;
            	$message='no se recibio imagen';
            	$foto=null;
	        }

	        $this->response(array('Estado' => $status,
		                          'Excepcion' => $message,
		                          'Fotos' => $foto),200);

         } catch (Exception $e) {
    		
 			$this->response(array('Estado' => Status_FAIL,
		                          'Excepcion' => $e->getMessage(),
		                          'Fotos' => null),200);
		}

    }


    private function _setImage($data) {

    	if( isset($data['Binary'])) {
			$binary = $data['Binary'];
		
			if ($binary === '' || $binary === 'NULL'){
    			$binary=null;
    		}
		}
		else{
			$binary = null;
		}

		return array('Direccion' => $data['Address'],
					 'Cliente' => $data['Client'],
					 'Binario' => $binary,
					 'Nombre' => $data['Name'],
					 'Ruta' => $data['Path'], 
					 'Nota' => $data['Note'],
					 'Estatus' => $data['Status']);
	} 

}