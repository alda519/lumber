sub mujtest()
  walkn(1870, 1458,"")
end sub

############################################################### 
#===---====----===---= LumberJacking =---=---=---=---=---=--==# 
#        New Lumberjacking script. DRW Shard www.drw.ru       # 
#                      Version 1.1                            # 
#           Scripted by Savage (c) 2004 ICQ:33336141          # 
#"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""# 
#"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""# 
# To start script after setuping, use ",exec lumber" command  # 
# from client window                                          # 
############################################################### 
#  Setup:                                                     # 
#  Bind NUM - key in client macro on targetself               # 
#  Bind NUM + key in injection hotkeys on exec onaddtree      # 
#  Bind NUM / key in injection hotkeys on exec onendrecord    # 
#  Equip char with: the hatchet or another chopping device,   # 
#  BM, MR, BP reagents 50-100 quantity, 3 blank rune.         # 
#  Go to unload position and execute: ,exec addunload.        # 
#  Choice rune and store container.                           # 
#  Go to start chopping position and execute: ,exec record.   # 
#  Choice rune. Press NUM + key to add tree. Walk to next     # 
#  chopping position and press NUM + key...                   # 
#  Press NUM / key to stop recording.                         # 
#  Execute: ,exec play to test walking. If char sucessfuly    # 
#  walked to end recording position, execute: ,exec lumber.   # 
#  Or record way again.                                       # 
#"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""# 

sub Lumber() 
   VAR f=file("records.txt") 
   VAR Buffer, LastTimer, ChopCount, X, Y, Tempx, TempY 
   VAR Exit, ExitLocal 
   VAR Tile, Xtree, YTree, ZTree, TileInfo 
   VAR WaitTime=150 ; Max time to chop one tree 
   VAR MaxWeight=440 ; (Max weight-20) 
   VAR MaxChopCount=45 ; Max times to chop one tree 

   AddHatchet() 
   ;wait(3000) 
   ;AddTemp() 

   UO.SetGlobal('EndRecord',"Off") 

   While NOT UO.Dead() AND UO.GetGlobal('EndRecord')=='Off' 
;      mrecal('StartRune') ; start position 

      f.Open() 
      Exit=0    
      While Exit==0 AND UO.GetGlobal('EndRecord')=='Off' 
;         tohide() 
         Buffer=f.ReadString() 
         If Buffer=="End" Then 
            Exit=1 
         Else 
            If Buffer=="Step" Then 
               X=f.ReadNumber() 
               Y=f.ReadNumber() 
               WalkN(X,Y,'') 
;               tohide() 
            Else 
               Tile=f.ReadNumber() 
               XTree=f.ReadNumber() 
               YTree=f.ReadNumber() 
               ZTree=f.ReadNumber() 
               ChopCount=0 
               ExitLocal=0 
               repeat 
                  CheckLag() 
                  UO.WaitTargetTile(STR(Tile),STR(XTree),STR(YTree),STR(ZTree)) 
                  uo.print('usetype')
                  UO.UseObject('Hatchet') 
       
                  ;LastTimer=UO.Timer() 
                  ;while NOT UO.InJournal("You put") AND NOT UO.InJournal("You hack") AND NOT UO.InJournal("There is nothing") AND NOT UO.InJournal("That's too") AND NOT UO.InJournal("It appears") AND NOT UO.InJournal("You can") AND NOT LastTimer+WaitTime<UO.Timer() 
                  ;   wait(100) 
                  ;wend 
                  LastTimer=UO.Timer() 
                  repeat
                     wait(100) 
                  until UO.InJournal("You put|You hack|There is|That's too|It appears|You can") or (LastTimer+WaitTime<UO.Timer())

       
                  If LastTimer+WaitTime<UO.Timer() Then 
                     ExitLocal=1 
                  Endif 
          
; tady teto casti nerozumim, tak jsem ji zakomentoval
;                  if UO.InJournal("You hack") then 
;                      repeat 
;                         wait(100) 
;                      until UO.InJournal("You put") OR UO.InJournal("but fail to") OR LastTimer+WaitTime<UO.Timer() 
;                  endif
;                  if UO.InJournal("You put the Ent") then 
;                     gong(2) 
;                     TempX=UO.GetX() 
;                     TempY=UO.GetY() 
;                     UO.SetGlobal('AddTree','Off') 
;                     UO.Print("Destroy the Ent manually and press NUM + key!") 
;                     Repeat 
;                        wait(1000) 
;                     Until UO.GetGlobal('AddTree')=="On" 
;                     WalkN(TempX,TempY,"") 
;                  endif 

                  ChopCount=ChopCount+1 

; vyber jestli sekakt strom uplne, nebo jen jednou       
;               until UO.InJournal("There is|That's too|It appears|You can") OR ChopCount>=MaxChopCount OR ExitLocal==1 ; takhle seka furt
               until UO.InJournal("There is|That's too|It appears|You can|You put") OR ChopCount>=MaxChopCount OR ExitLocal==1 ; takhle seka jednou

               if UO.Weight>MaxWeight then 
                  unload() 
               endif 
            Endif 
         EndIf 
      Wend 
   Wend 
   f.Close() 
end sub 

sub mrecal(rune) 
   repeat 
      CheckLag() 
      UO.WaitTargetObject(rune) 
      UO.Cast('Recal') 
      wait(5000) 
   until NOT UO.InJournal("spell fizzles") 

markrune: 
   If UO.InJournal("fades completely")   then 
      CheckLag() 
      UO.WaitTargetObject(rune) 
      UO.Cast('Mark') 
   endif 
   wait(5000) 
   If UO.InJournal("spell fizzles") then 
      goto markrune 
   endif 
end sub 

sub addstart() 
   CheckLag() 
   UO.UseObject('StartRune') 
   Repeat 
      wait(500) 
   Until UO.InJournal('You can') OR UO.InJournal('What is the') 
    
   If UO.InJournal('You can') Then 
      UO.Print('Select a StartRune. Vibery runu!') 
      UO.AddObject('StartRune') 
      While UO.Targeting()==2 
         wait(500) 
      Wend 
markrune: 
      CheckLag() 
      UO.WaitTargetObject('StartRune') 
      UO.Cast('Mark') 
      wait(4000) 
      If UO.InJournal("spell fizzles") then 
         goto markrune 
      endif    
      UO.UseObject('StartRune') 
      UO.SaveConfig() 
   Else 
      UO.Print("Ok! StartRune is found. Nashel StartRunu.") 
   Endif 
   wait(1000) 
   UO.Say('StartRune') 
end sub 

sub addtemp() 
   CheckLag() 
   UO.UseObject('TempRune') 
   Repeat 
      wait(500) 
   Until UO.InJournal('You can') OR UO.InJournal('What is the') 
    
   If UO.InJournal('You can') Then 
      UO.Print('Select a TempRune. Vibery vremennuyu runu!') 
      UO.AddObject('TempRune') 
      While UO.Targeting()==2 
         wait(500) 
      Wend 
      UO.UseObject('TempRune') 
      UO.SaveConfig() 
   Else 
      UO.Print("Ok! TempRune is found. Nashel TempRunu.") 
   Endif 
   wait(1000) 
   UO.Say('TempRune') 
end sub 

sub addunload() 
   CheckLag() 
   UO.UseObject('UnloadRune') 
   Repeat 
      wait(500) 
   Until UO.InJournal('You can') OR UO.InJournal('What is the') 
    
   If UO.InJournal('You can') Then 
      UO.Print('Select a UnloadRune. Vibery runu razgruzki!') 
      UO.AddObject('UnloadRune') 
      While UO.Targeting()==2 
         wait(500) 
      Wend 
markrune: 
      CheckLag() 
      UO.WaitTargetObject('UnloadRune') 
      UO.Cast('Mark') 
      wait(4000) 
      If UO.InJournal("spell fizzles") then 
         goto markrune 
      endif    
      UO.UseObject('UnloadRune') 
      UO.SaveConfig() 
   Else 
      UO.Print("Ok! UnloadRune is found. Nashel UnloadRunu.") 
   Endif 
   wait(1000) 
   UO.Say('UnloadRune') 
   AddStore() 
end sub 


sub addstore() 
   Var LastTimer 
   CheckLag() 
   UO.UseObject('StoreCont') 
   LastTimer=UO.Timer() 
   Repeat 
      wait(500) 
   Until UO.InJournal("You can't see") OR UO.InJournal("You can't reach") OR UO.InJournal('What is the') OR LastTimer+30<UO.Timer() 
    
   If UO.InJournal("You can't see") Then 
      UO.Print('Select a StoreContainer. Vibery sunduk dlya logov!') 
      UO.AddObject('StoreCont') 
      While UO.Targeting()==2 
         wait(500) 
      Wend 
      UO.UseObject('StoreCont') 
      UO.SaveConfig() 
   Else 
      UO.Print("Ok! StoreCont is added. Est sunduk!") 
   Endif 
end sub 

sub addhatchet() 
   CheckLag() 
   UO.UseObject('Hatchet') 
   Repeat 
      wait(500) 
   Until UO.InJournal('You can') OR UO.InJournal('What is the') OR UO.InJournal('What do you') 
    
   If UO.InJournal('You can') Then 
      UO.Print('Select a Hatchet. Vibery topor!') 
      UO.AddObject('Hatchet') 
      While UO.Targeting()==2 
         wait(500) 
      Wend 
      UO.SaveConfig() 
   Else 
; Bind targetself on NUM - in client macro 
      UO.Press(220) ; Otmena targeta 
   Endif 
   UO.Print("Ok! Hatchet is found. Nashel Topor.") 
end sub 

sub unload() 
markrune: 
   CheckLag() 
   UO.WaitTargetObject('TempRune') 
   UO.Cast('Mark') 
   wait(5000) 
   If UO.InJournal("spell fizzles") then 
      goto markrune 
   endif    
   mrecal('UnloadRune') ; home 
   logunload() 
   mrecal('TempRune') 
end sub 

sub logunload() 
   VAR WaitTime=1200 
;   ToHide() 
Begin: 
   UO.FindType('0x1BDD') ; logs 
   if UO.GetQuantity('finditem')>0 then 
      UO.MoveItem('finditem','0','StoreCont') 
      CheckLag() 
      wait(WaitTime) 
      goto Begin 
   endif 
end sub 

sub OnAddTree() 
; Assign on Num + in hotkeys 
   UO.SetGlobal('AddTree','On') 
end sub 

sub OnEndRecord() 
; Assign on Num / in hotkeys 
   UO.SetGlobal('EndRecord','On') 
end sub 

sub Record() 
   VAR f=file("records.txt") 
   VAR TileInfo, Exit=0, X,Y, i=0 
    
   UO.SetGlobal('AddTree','Off') ; Num + 
   UO.SetGlobal('EndRecord','Off') ; Num / 
    
   f.Create() 
   f.Open() 
    
   ;AddStart() 
   While Exit==0 

      X=UO.GetX() 
      Y=UO.GetY() 
       
      UO.Print("Press command key! Davi knopku!") 

      While UO.GetGlobal('AddTree')=='Off' AND UO.GetGlobal('EndRecord')=='Off' 
         wait(500) 
      Wend 

      If UO.GetGlobal('EndRecord')=='On' Then 
         Exit=1 
      Endif 

      If UO.GetGlobal('AddTree')=='On' Then 
         UO.SetGlobal('AddTree','Off') 

         If UO.GetX()<>X OR UO.GetY()<>Y Then 
            UO.Print("Writing waypoint. Zapisivayu koordinaty!") 

            f.WriteLn("Step") 
            f.WriteLn(UO.GetX()) 
            f.WriteLn(UO.GetY()) 
         Endif 

         UO.Info() 
         UO.Print("Click on tree. Tkni v derevo!") 

         While UO.Targeting()==2 
            wait(500) 
         Wend 

         TileInfo=UO.LastTile() 
         f.WriteLn("Chop") 
         f.WriteLn(TileInfo) 
         i=i+1 
      Endif 
   Wend 
   f.WriteLn("End") 
   f.Close() 
   UO.Print("Recording stopped. Zapis' zavershena.") 
   UO.Print("Count of added trees="+STR(i)) 
   UO.Print("Dobavleno derev'ev="+STR(i)) 
end sub 

sub Play() 
   VAR f=file("records.txt") 
   VAR X,Y, Buffer, Exit=0 
    
   mrecal('StartRune') 
   f.Open() 
   UO.SetGlobal('EndRecord','Off') ; Num / 

   While Exit==0 AND UO.GetGlobal('EndRecord')=='Off' 
      Buffer=f.ReadString() 
      If Buffer=="End" Then 
         Exit=1 
      Else 
         If Buffer=="Step" Then 
            X=f.ReadNumber() 
            Y=f.ReadNumber() 
            WalkN(X,Y,'') 
            wait(3000) 
         Else 
            If NOT f.EOF() Then 
               Buffer=f.ReadLn() 
            Endif 
         EndIf 
      EndIf 
   Wend 
   UO.Print("Playing stopped. Proverka zavershena.") 
   f.Close() 
end sub 
############################################################### 
;                        Shared Subs 
############################################################### 
#============================================================== 
#  tohide() - try hidding char. When char lose HP, drink inviz 
#             potion if it found in backpack 
#  Journal has been deleted!!! 
#-------------------------------------------------------------- 
sub tohide() 
   While NOT UO.Hidden() 
      UO.DeleteJournal() 
         UO.Exec('warmode 0') 
         UO.UseSkill('Stealth') 
         Repeat 
            wait(100) 
         Until UO.InJournal('You have hidden') OR UO.InJournal('seem to hide') OR UO.InJournal('preoccupied') 
   Wend 
end sub 

#============================================================== 
#  CheckLag() - click on backpack and awaiting "backpack" 
#               message in journal. 
# 
#  Journal has been deleted!!! 
#-------------------------------------------------------------- 
sub CheckLag() 
   UO.DeleteJournal() 
   UO.Click('backpack') 

   Repeat 
      wait(200) 
   Until UO.InJournal('backpack') 
end sub 

#============================================================== 
#  Gong(counter) - playing wav-file 'counter'-times 
#  http://www.jetta.ru/cow.wav 
#-------------------------------------------------------------- 
sub Gong(times) ; play wav-file 
   VAR i 
   For i=1 to times 
      UO.Exec("playwav C:\hry\injection\Sounds\cow.wav") 
      wait(1200) ; time to play sample at once 
   Next 
end sub 

sub tst() 
   WalkN(2102,2082,'') 
end sub 


sub absval(a)
  if a > 0 then
    return a
  else
    return 0-a
  endif
end sub

#============================================================== 
#  WalkN(X,Y,Serial) - char is walking by dX and dY step 
#                sub using Home, End, PgUp, PgDown keys 
#                d'not rebind this key from default action! 
#       serial - Serial of target or "" - string 
#     walkwait - delay after keypress 
#     Example: 
#     WalkN(2080,2113,'') - go to coordinates 
#     WalkN(0,0,'0x12345678') - go to target position 
#-------------------------------------------------------------- 
sub WalkN(x,y,Target) 
   VAR i,StepSucess 
   VAR dx,dy,Exit=0 
   var stepdelay = 50 ; mozno nastavit
    
   While Exit<>1    
      If Target<>"" Then 
         dx=UO.GetX(Target)-UO.GetX() 
         dy=UO.GetY(Target)-UO.GetY() 
;         UO.Print("Target locked!") 
         If UO.GetDistance(Target)<2 Then 
            Exit=1 
         Endif 
      Else 
         dx=x-UO.GetX() 
         dy=y-UO.GetY() 
         If dx==0 AND dy==0 Then 
            Exit=1 
         Endif
         if (1 == absval(dx) or dx == 0) and (dy == 0 or absval(dy) == 1) then 
           Exit=1 
         endif
      Endif 
    
      If dx<>0 AND dy<>0 Then 
         If dx>0 AND dy>0 Then 
            StepSucess=Go(3,40,stepdelay) ;SE - DownArrow 
            If StepSucess==-1 Then 
               StepSucess=Go(7,38,stepdelay) ;WN - UpArrow 
               StepSucess=Go(1,39,stepdelay) ;NE - RightArrow 
               If StepSucess==-1 Then 
                  StepSucess=Go(5,37,stepdelay) ;SW - LeftArrow 
               Endif 
            Endif 
         Endif 

         If dx>0 AND dy<0 Then 
            StepSucess=Go(1,39,stepdelay) ;NE - RightArrow 
            If StepSucess==-1 Then 
               StepSucess=Go(5,37,stepdelay) ;SW - LeftArrow 
               StepSucess=Go(3,40,stepdelay) ;SE - DownArrow 
               If StepSucess==-1 Then 
                  StepSucess=Go(7,38,stepdelay) ;WN - UpArrow 
               Endif 
            Endif 
         Endif 

         If dx<0 AND dy>0 Then 
            StepSucess=Go(5,37,stepdelay) ;SW - LeftArrow 
            If StepSucess==-1 Then 
               StepSucess=Go(1,39,stepdelay) ;NE - RightArrow 
               StepSucess=Go(7,38,stepdelay) ;WN - UpArrow 
               If StepSucess==-1 Then 
                  StepSucess=Go(3,40,stepdelay) ;SE - DownArrow 
               Endif 
            Endif 
         Endif 

         If dx<0 AND dy<0 Then 
            StepSucess=Go(7,38,stepdelay) ;WN - UpArrow 
            If StepSucess==-1 Then 
               StepSucess=Go(3,40,stepdelay) ;SE - DownArrow 
               StepSucess=Go(5,37,stepdelay) ;SW - LeftArrow 
               If StepSucess==-1 Then 
                  StepSucess=Go(1,39,stepdelay) ;NE - RightArrow 
               Endif 
            Endif 
         Endif 

      Endif 
    
      If dx<>0 AND dy==0 Then 
         If dx>0 Then 
            StepSucess=Go(2,34,stepdelay) ;E - PgDown 
            If StepSucess==-1 Then 
               StepSucess=Go(3,40,stepdelay) ;SE - DownArrow 
               If StepSucess==-1 Then 
                  StepSucess=Go(1,39,stepdelay) ;NE - RightArrow 
               Endif 
               StepSucess=Go(2,34,stepdelay) ;E - PgDown 
            Endif 
         Endif 
          
         If dx<0 Then 
            StepSucess=Go(6,36,stepdelay) ;W - Home 
            If StepSucess==-1 Then 
               StepSucess=Go(7,38,stepdelay) ;WN - UpArrow 
               If StepSucess==-1 Then 
                  StepSucess=Go(5,37,stepdelay) ;SW - LeftArrow 
               Endif 
               StepSucess=Go(6,36,stepdelay) ;W - Home 
            Endif 
         Endif 
      Endif 

      If dx==0 AND dy<>0 Then 
         If dy>0 Then 
            StepSucess=Go(4,35,stepdelay) ;S - End 
            If StepSucess==-1 Then 
               StepSucess=Go(3,40,stepdelay) ;SE - DownArrow 
               If StepSucess==-1 Then 
                  StepSucess=Go(5,37,stepdelay) ;SW - LeftArrow 
               Endif 
               StepSucess=Go(4,35,stepdelay) ;S - End 
            Endif 
         Endif 
          
         If dy<0 Then 
            StepSucess=Go(0,33,stepdelay) ;N - PgUp 
            If StepSucess==-1 Then 
               StepSucess=Go(1,39,stepdelay) ;NE - RightArrow 
               If StepSucess==-1 Then 
                  StepSucess=Go(7,38,stepdelay) ;WN - UpArrow 
               Endif 
               StepSucess=Go(0,33,stepdelay) ;N - PgUp 
            Endif 
         Endif 
      Endif 
   Wend 
end sub 

sub Go(dir,key,walkwait) 
   VAR x,y, OldDir 

   x=UO.GetX() 
   y=UO.GetY() 
   OldDir=UO.GetDir() 

   If UO.GetDir()<>dir Then 
      UO.Press(key) 
      wait(walkwait) 
      If UO.GetDir()<>dir Then 
         CheckLag() 
      Endif 
   Endif 

   UO.Press(key) 
   wait(walkwait) 

   If x==UO.GetX() AND y==UO.GetY() Then 
      CheckLag() 
   Endif 

   If x==UO.GetX() AND y==UO.GetY() AND OldDir<>UO.GetDir() Then 
      UO.Press(key) 
      wait(walkwait) 
   Endif 

   If x==UO.GetX() AND y==UO.GetY() Then 
      CheckLag() 
   Endif 
    
   If x==UO.GetX() AND y==UO.GetY() Then 
      UO.Print("Zasada!") 
      return -1 
   Else 
      return 1 
   Endif 
end sub