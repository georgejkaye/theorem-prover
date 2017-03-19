convert :: String -> String
convert [] = []
convert ('-' : '>' : xs) = '-' : '>' : convert xs
convert ('-' : xs) = "not " ++ convert xs
convert ('T' : xs) = 'G' : convert xs
convert ('&' : xs) = '&' : '&' : convert xs
convert ('|' : xs) = '|' : '|' : convert xs 
convert (x : xs) = x : convert xs
