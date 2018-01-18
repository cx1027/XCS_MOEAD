library(dplyr)
library(stringr)
library(ggplot2)


############ settings ##############
maze4 <- c('maze4_path_weight100_weight30.csv','Maze4 - 201801162151 - Trial 0 - TRIAL_NUM - 6000 - TEST.csv') #Maze4 - 201801162151 - Trial 0 - TRIAL_NUM - 6000 - TEST.csv')
maze5 <- c('maze5_path_weight100_weight30.csv','Maze5- 201801162156 - Trial 0 - TRIAL_NUM - 6000 - TEST.csv') #Maze5 - 201801162151 - Trial 0 - TRIAL_NUM - 6000 - TEST.csv')
maze6 <- c('maze6_path_weight100_weight30.csv','Maze6 - 201801161300 - Trial 0 - TRIAL_NUM - 6000 - TEST.csv') #Maze6 - 201801151104 - Trial 0 - TRIAL_NUM - 6000 - TEST.csv.csv')


upperBound <- 6000
traceWeightFilter <- c('0.040000|0.960000'
                       , '0.480000|0.520000'
                       #, '0.520000|0.480000'
                       , '0.960000|0.040000') #c('0.000000|1.000000', '0.560000|0.440000', '1.000000|0.000000')

plot.labels <- list(expression(paste(lambda[1],'=0.04, 0.96','  ',sep=''))
                    , expression(paste(lambda[2],'=0.48, 0.52','  ',sep=''))
                    #, expression(paste(lambda[3],'=0.52, 0.48',sep=''))
                    , expression(paste(lambda[3],'=0.96, 0.04','  ',sep='')) )

##################
mazeToRun <- maze6

############# begin to read result #############
setwd("C:/Users/martin.xie/IdeaProjects/XCS_MOEAD/dataAnalysis/Maze")

targetSteps <- read.csv(file = mazeToRun[1], header = TRUE, sep = ",", stringsAsFactors = FALSE)
targetId <- paste(targetSteps$open, targetSteps$final, paste(as.character(targetSteps$step), '', sep = ''), sep = '*')
targetSteps <- cbind(targetSteps, targetId)


setwd("C:/Users/martin.xie/IdeaProjects/XCS_MOEAD/log/MOXCS")
raw.data <- read.csv(file =   mazeToRun[2] #Train - 201801141417 - Trial 0 - TRIAL_NUM - 6000 - TEST.csv.csv"
                     , header = TRUE, sep = ","
                     , stringsAsFactors = FALSE)


data <- raw.data %>% 
    select(TrailNumber, Timestamp, TargetWeight, TraceWeight, obj_r1, OpenState, FinalState, steps, hyperVolumn, path) %>%
    #filter(TraceWeight == ' 0.000000|1.000000')
    #%>% filter(TrailNumber == 0)
    filter(TraceWeight %in% traceWeightFilter
          , Timestamp <= upperBound)

#max(data$Timestamp)
#unique(raw.data$TraceWeight)
#unique(data$TraceWeight)


uid <- paste(trimws(data$OpenState), trimws(data$FinalState), data$steps, sep = "*")
data <- cbind(data, uid)
rm(uid)

result <- data %>%
            select(TrailNumber, Timestamp,TargetWeight,TraceWeight,hyperVolumn ) %>%distinct

 
getMatchCountForRow <- function(arow, data) {
    trows <- data %>% 
            filter(TrailNumber == arow$TrailNumber
                        ,Timestamp == arow$Timestamp
                        , TargetWeight == arow$TargetWeight
                        , TraceWeight == arow$TraceWeight
                        )
    #print(nrow(trows))
    match <- 0
    for (i in 1:nrow(trows)) {
        #print(trows[i,]$uid)
        match <- match + ifelse(trows[i,]$uid %in% targetSteps$targetId, 1, 0)
    }
    #print(paste(arow$TrailNumber, ':', arow$Timestamp, ':'
    #            , arow$TargetWeight, ':'
    #            , arow$TraceWeight, ':'
    #            , match, '/', nrow(trows), sep = ' '))
    (match/nrow(trows))
}

#match.rate <- getMatchCountForRow(result[1800,], data)

#lapply(result, getMatchCountForRow, data)
result$matchRate <- 0
for (i in 1:nrow(result)) {
    #print(getMatchCountForRow(result[i,], data))
    result[i,]$matchRate <- getMatchCountForRow(result[i,], data)
}

##result[1,]$matchRate <- getMatchCountForRow(result[1,], data)

uniqTrail <- unique(result$TrailNumber)
pall <- rep(NULL, nrow(uniqTrail))
pdata <- NULL

for (i in uniqTrail) {
    pdata <- result %>%
        filter(TrailNumber == i
        #, TraceWeight == '5.000000|5.000000'
        #, TraceWeight == uniqWeight[i] #' 0.000000|1.000000'
        )
    plt <- ggplot(pdata, aes(x = Timestamp, y = matchRate, group = TraceWeight, color = TraceWeight, linetype = TraceWeight)) +
        geom_line()
    print(plt)
}

#ggplot(pdata, aes(x = Timestamp, y = matchRate, group = TraceWeight, color = c('#41ae76', '#ef6548', '#4292c6'))) +
#    geom_line()



#retdata <- result %>%
#    group_by(Timestamp, TargetWeight, TraceWeight, hyperVolumn) %>%
#    summarise(avgmr = mean(matchRate))

retdata <- result %>%
  group_by(Timestamp, TargetWeight, TraceWeight) %>% distinct


getMatchCountForRow_avg <- function(arow, data) {
  trows <- data %>%
    filter(Timestamp == arow$Timestamp, TargetWeight == arow$TargetWeight, TraceWeight == arow$TraceWeight)
  
  mean(trows$matchRate)
}


getMatchCountForRow_avgHV <- function(arow, data) {
  trows <- data %>%
    filter(Timestamp == arow$Timestamp, TargetWeight == arow$TargetWeight, TraceWeight == arow$TraceWeight)
  
  mean(trows$hyperVolumn)
}



#arow <- retdata[100,]
#trows <- result %>%
#            filter(Timestamp == arow$Timestamp, TargetWeight == arow$TargetWeight, TraceWeight == arow$TraceWeight)

avgRate <- rep(0,nrow(retdata))
for (i in 1:nrow(retdata)) {
  #print(getMatchCountForRow_avg(retdata[i,], result))
  avgRate[i] <- getMatchCountForRow_avg(retdata[i,], result)
}

hyperVolumn <- rep(0,nrow(retdata))
for (i in 1:nrow(retdata)) {
  #print(getMatchCountForRow_avg(retdata[i,], result))
  hyperVolumn[i] <- getMatchCountForRow_avgHV(retdata[i,], result)
}

getMatchCountForRow_avg(retdata[2,], result)

retdata$matchRate <- avgRate
retdata$hyperVolumn <- hyperVolumn

plt <- ggplot(retdata, aes(x = Timestamp, y = matchRage, group = TraceWeight, color = TraceWeight, linetype = TraceWeight)) +
    geom_line()

ggplot(retdata, aes(x = Timestamp, y = hyperVolumn, group = TraceWeight, color = TraceWeight, linetype = TraceWeight)) +
geom_line()



########### plot begin ###########
theme_set(theme_classic(base_size = 9))


lty = c(1, 2, 3, 4, 5, 6, 8, 9, 1)
lshp = c(1, 2, 3, 4, 5, 6, 7, 8, 9)
cbbPalette = c('#e41a1c', '#377eb8', '#4daf4a', '#984ea3', '#ff7f00', '#66ff66', '#a65628', '#f781bf', '#000000')


plot.data <- retdata %>% filter(TraceWeight  %in% plot.traceWeightFilter
                                , Timestamp <= plot.upperBound
)

#%>%filter(TraceWeight=='0.480000|0.520000',Timestamp<500,Timestamp>200)
phv <- ggplot(data = plot.data, aes(
  x = Timestamp,
  y = hyperVolumn,
  colour = TraceWeight,
  group = TraceWeight,
  linetype = TraceWeight
  )) +
  geom_line() +
  labs(x = 'Number of Leaning Problems\n(a)', y = NULL) +
  ggtitle("THV") +
  theme(axis.title.y = element_text(size = rel(1.1), face = "bold"), axis.title.x = element_text(size = rel(1.1), face = "bold"), title = element_text(size = rel(1.1), face = 'bold')) +
  theme(legend.text = element_text(size = rel(1), face = "bold")) +
  theme(legend.title = element_blank()) +
  #theme(legend.position = c(0.63, 0.15))
theme(legend.position = 'bottom') + theme(panel.grid.major = element_line(size = 0.01, linetype = 'dotted',
                                                                           colour = "black"),
                                           panel.grid.minor = element_line(size = 0.001, linetype = 'dotted',
                                                                           colour = "black")) +
  theme(legend.background = element_rect(fill = alpha('gray', 0.05))) +
  theme(axis.text.x = element_text(size = rel(1.4)),
        axis.text.y = element_text(size = rel(1.4)),
        axis.title = element_text(size = rel(1.2), face = "bold")) +
  scale_linetype_manual(values = lty, guide = "none") +
  scale_colour_manual(values = cbbPalette, labels = plot.labels) +
  guides(colour=guide_legend(override.aes=list(linetype=1:3)))

 
pmr <- ggplot(data = plot.data, aes(
  x = Timestamp,
  y = matchRate,
  colour = TraceWeight,
  group = TraceWeight,
  linetype = TraceWeight)) +
  geom_line() +
  labs(x = 'Number of Leaning Problems\n(a)', y = NULL) +
  ggtitle("% OP") +
  theme(axis.title.y = element_text(size = rel(1.1), face = "bold"), axis.title.x = element_text(size = rel(1.1), face = "bold"), title = element_text(size = rel(1.1), face = 'bold')) +
  theme(legend.text = element_text(size = rel(1), face = "bold")) +
  theme(legend.title = element_blank()) +
  #theme(legend.position = c(0.63, 0.15))
theme(legend.position = 'bottom') + theme(panel.grid.major = element_line(size = 0.01, linetype = 'dotted',
                                                                           colour = "black"),
                                           panel.grid.minor = element_line(size = 0.001, linetype = 'dotted',
                                                                           colour = "black")) +
  theme(legend.background = element_rect(fill = alpha('gray', 0.05))) +
  theme(axis.text.x = element_text(size = rel(1.4)),
        axis.text.y = element_text(size = rel(1.4)),
        axis.title = element_text(size = rel(1.2), face = "bold")) +
  scale_linetype_manual(values = lty) +
  scale_colour_manual(values = cbbPalette)

library(gridExtra)

g_legend <- function(a.gplot) {
    tmp <- ggplot_gtable(ggplot_build(a.gplot))
    leg <- which(sapply(tmp$grobs, function(x) x$name) == "guide-box")
    legend <- tmp$grobs[[leg]]
    return(legend)
}

mylegend <- g_legend(phv)

p3 <- grid.arrange(arrangeGrob(phv + theme(legend.position = "none"),
                               pmr + theme(legend.position = "none"),
                               nrow = 1),
                   mylegend, nrow = 2, heights = c(5, 1) )

p3