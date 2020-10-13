<?php
/**
* 
*/
require APPPATH . 'libraries/REST_Controller.php';
class Upload extends REST_Controller 
{
	
	function __construct()
	{
		parent::__construct();
	}

	function upload_post() { -
      
        $this->response(array('Message' => 'opcion -01'),200);  

        if ( !empty($_FILES) ) {

            echo json_encode(array('Message' => 'opcion 00'));    

            $fileName = $_FILES['file']['name'];
            $config['upload_path'] = '../pictures/';
            $config['file_name'] = $fileName;
            $config['allowed_types'] = 'jpg|png';
            $config['encrypt_name'] = TRUE;
            $config['file_ext_tolower']=TRUE;
            $this->load->library('upload', $config); 

            if (!$this->upload->do_upload("file")) {

                $this->response(array('Message' => 'opcion 01'),200);   


                $status['error'] =$this->upload->display_errors('', '');
                $status['status'] = 2;
            } else {
                
                echo json_encode(array('Message' => 'opcion 02'),200); 

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
            }
        } else {

            $this->response(array('Message' => 'opcion 03'),200); 
            $status['error']='Pilih Foto';
            $status['status'] = 3;
        }
        $this->response($status);
    }
}