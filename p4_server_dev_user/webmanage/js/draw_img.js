
function pic_reset(drawImage,thumbs_size) {    
    var max = thumbs_size.split(',');    
    var fixwidth = max[0];    
    var fixheight = max[1];   
    w=drawImage.width;h=drawImage.height;    
    if(w>fixwidth) { drawImage.width=fixwidth;drawImage.height=h/(w/fixwidth);}    
    if(h>fixheight) { drawImage.height=fixheight;drawImage.width=w/(h/fixheight);}          
    drawImage.style.cursor= "pointer";    
    drawImage.onclick = function() { window.open(this.src);}     
    drawImage.title = "点击查看原始图片";   
}

var flag=false; 
function DrawImage(ImgD,w,h){ 
var image=new Image(); 
image.src=ImgD.src; 
if(image.width>0 && image.height>0){ 
flag=true; 
if(image.width/image.height>= w/h){ 
    if(image.width>w){ 
      ImgD.width=w; 
      ImgD.height=(image.height*w)/image.width; 
    }else{ 
      ImgD.width=image.width; 
      ImgD.height=image.height; 
    } 
}else{ 
    if(image.height>h){ 
      ImgD.height=h; 
      ImgD.width=(image.width*h)/image.height; 
    }else{ 
      ImgD.width=image.width; 
      ImgD.height=image.height; 
    } 
} 
} 
} 
