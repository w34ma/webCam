ReadMe - WebCam

Ensure the sever that will send vrmsg has already started before run the WebCam.

Run vcCameraMain.java to start the WebCam
	-once received the "vrCamera start", the WebCam start recording,
	-once received the "vrCamera end", the WebCam end recording

Note:
1. Change IP address in Config.java to the IP of the computer that will be responsible for sending vr msg

2. In open_cvCamera.java, change line 91 to be the path where you want to store the video, and line 92 don't need to be changed. Line 92 will reproduce mp4 format for the recording [Note:ffmpeg need to be installed]

3.In MyRecord.java, change line 104 to be the path where you want to store the audio.

4.Turn off the laptop microphone and set the defualt microphone as WebCam 's microphone will get better quality. 

5.Final output, 
	-video in avi format without audio
	-audio in mp3 format without video

6.Reproduce video to mp4 format
	Open terminal and type command:
		ffmpeg -i video.avi -c:v libx264 -preset slow -crf 19 -c:a libvo_aacenc -b:a 128k new_video.mp4

7.Merge audio and video:
	Open terminal and type command:
		ffmpeg -i new_video.mp4 -i audio.mp3 -c:v copy -c:a aac -strict experimental merge.mp4

8.Problem: 
	The auido and video is not synced and the longer recording we have the longer delay we have.
	And that is no linear.